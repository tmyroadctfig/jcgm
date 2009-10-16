/*
 * Copyright (c) 2009, Swiss AviationSoftware Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Swiss AviationSoftware Ltd. nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.sf.jcgm.imageio.plugins.cgm;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import net.sf.jcgm.core.CGM;
import net.sf.jcgm.core.CGMDisplay;
import net.sf.jcgm.core.Message;

/**
 * Image reader for the CGM format.
 * <p>
 * <a href=
 * "http://java.sun.com/javase/6/docs/technotes/guides/imageio/spec/title.fm.html"
 * >JavaTM Image I/O API Guide</a>
 * 
 * @author xphc (Philippe Cadé)
 * @version $Id$
 */
class CGMImageReader extends ImageReader {
	
	private CGM cgm = null;
	private Dimension size;
	private CGMMetadata metadata;
	
	public CGMImageReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	@Override
	public int getHeight(int imageIndex) throws IIOException {
		checkIndex(imageIndex);
		readHeader();
		return this.size.height;
	}

	@Override
	public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
		if (this.metadata != null) {
			// return the cached meta data
			return this.metadata;
		}
		
		checkIndex(imageIndex);
		readHeader();
		
		this.metadata = new CGMMetadata(); 
		return this.metadata;
	}
	
	@Override
	public ImageReadParam getDefaultReadParam() {
		return new CGMImageReadParam();
	}

	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) {
		checkIndex(imageIndex);

		// XXX: is this right?
		int datatype = DataBuffer.TYPE_BYTE;
		ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		int[] bandOffsets = new int[3];
		bandOffsets[0] = 0;
		bandOffsets[1] = 1;
		bandOffsets[2] = 2;
		ImageTypeSpecifier imageType =
			ImageTypeSpecifier.createInterleaved(rgb,
			                                     bandOffsets,
			                                     datatype,
			                                     false,
			                                     false);
		
		List<ImageTypeSpecifier> list = new ArrayList<ImageTypeSpecifier>(1);
		list.add(imageType);
		return list.iterator();
	}

	@Override
	public int getNumImages(boolean allowSearch) {
		// XXX: only support one image for now, although CGM supports multiple
		// images
		return 1;
	}

	/**
	 * Not supported.
	 * @return null
	 */
	@Override
	public IIOMetadata getStreamMetadata() {
		return null;
	}

	@Override
	public int getWidth(int imageIndex) throws IIOException {
		checkIndex(imageIndex);
		readHeader();
		return this.size.width;
	}

	@Override
	public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
		checkIndex(imageIndex);
		
		if (!(param instanceof CGMImageReadParam))
			throw new IllegalArgumentException("unexpected ImageReadParam type. Must be CGMImageReadParam");
		
		CGMImageReadParam cgmParam = (CGMImageReadParam)param;

		if (this.cgm == null) {
			ImageInputStream stream = (ImageInputStream)this.input;
			this.cgm = new CGM();
			this.cgm.read(stream);
			//stream.close();
		}

		// publish the error messages that happened during the load
		cgmParam.setMessages(this.cgm.getMessages());
		for (Message m: this.cgm.getMessages()) {
			processWarningOccurred(m.toString());
		}
		
		this.size = this.cgm.getSize(cgmParam.getDPI());
		if (this.size == null) {
			// the CGM doesn't know its size, use a default
			this.size = new Dimension(600, 400);
		}
		
		BufferedImage destination = getDestination(param, getImageTypes(0), this.size.width, this.size.height);
		CGMDisplay display = new CGMDisplay(this.cgm);
		Graphics graphics = destination.getGraphics();
		display.scale(graphics, this.size.width, this.size.height);
		display.paint(graphics);
		
		return destination;
	}

	/**
	 * Checks the image index to make sure it lays within range
	 * @param imageIndex The index to check
	 */
	private void checkIndex(int imageIndex) {
		if (imageIndex != 0) {
			// we're only supporting one image for now
			throw new IndexOutOfBoundsException("bad index");
		}
	}

	/**
	 * Reads the image if it hasn't been read before. The size of the image will
	 * be set (using default DPI).
	 * 
	 * @throws IIOException
	 *             If there was an error reading the image
	 */
	private void readHeader() throws IIOException {
		if (this.cgm != null) {
			// we have already read the header
			return;
		}
		
		if (this.input == null || !(this.input instanceof ImageInputStream)) {
			throw new IllegalStateException("stream is not set or wrong type");
		}
		ImageInputStream stream = (ImageInputStream)this.input;
		try {
			this.cgm = new CGM();
			this.cgm.read(stream);
			this.size = this.cgm.getSize();
		}
		catch (IOException e) {
			throw new IIOException("IOException", e);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		this.cgm = null;
	}
}

/*
 * vim:encoding=utf8
 */
