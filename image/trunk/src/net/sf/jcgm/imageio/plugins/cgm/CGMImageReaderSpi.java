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

import static net.sf.jcgm.core.MIMETypes.CGM_MIME_Types;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * Provides information about the plugin.
 * @author xphc (Philippe Cadé)
 * @version $Id$
 */
public class CGMImageReaderSpi extends ImageReaderSpi {
	static final String vendorName = "Swiss AviationSoftware Ltd.";
	static final String version = "1";
	static final String readerClassName =
		"net.sf.jcgm.imageio.plugins.cgm.CGMImageReader";
	static final String[] names = { "CGM" };
	static final String[] suffixes = { "cgm" };
	static final String[] writerSpiNames = null;

	// Metadata formats, more information below
	static final boolean supportsStandardStreamMetadataFormat = false;
	static final String nativeStreamMetadataFormatName = null;
	static final String nativeStreamMetadataFormatClassName = null;
	static final String[] extraStreamMetadataFormatNames = null;
	static final String[] extraStreamMetadataFormatClassNames = null;
	static final boolean supportsStandardImageMetadataFormat = false;
	static final String nativeImageMetadataFormatName =
		"net.sf.jcgm.imageio.plugins.cgm.CGMMetadata_1.0";
	static final String nativeImageMetadataFormatClassName =
		"net.sf.jcgm.imageio.plugins.cgm.CGMMetadata";
	static final String[] extraImageMetadataFormatNames = null;
	static final String[] extraImageMetadataFormatClassNames = null;

	public CGMImageReaderSpi() {
		super(vendorName, version,
		      names, suffixes, CGM_MIME_Types,
		      readerClassName,
		      STANDARD_INPUT_TYPE, // Accept ImageInputStreams
		      writerSpiNames,
		      supportsStandardStreamMetadataFormat,
		      nativeStreamMetadataFormatName,
		      nativeStreamMetadataFormatClassName,
		      extraStreamMetadataFormatNames,
		      extraStreamMetadataFormatClassNames,
		      supportsStandardImageMetadataFormat,
		      nativeImageMetadataFormatName,
		      nativeImageMetadataFormatClassName,
		      extraImageMetadataFormatNames,
		      extraImageMetadataFormatClassNames);
	}


	@Override
	public boolean canDecodeInput(Object input) {
		if (!(input instanceof ImageInputStream)) {
			return false;
		}
		
		ImageInputStream stream = (ImageInputStream)input;
		byte[] b = new byte[2];
		try {
			stream.mark();
			stream.readFully(b);
			stream.reset();
		}
		catch (IOException e) {
			return false;
		}
		
		return b[0] == 0x00 && (b[1] & 0xE0) == 0x20;
	}

	@Override
	public ImageReader createReaderInstance(Object extension) {
		return new CGMImageReader(this);
	}

	@Override
	public String getDescription(Locale locale) {
		return "CGM (Computer Graphics Metafile) Image Reader";
	}

}

/*
 * vim:encoding=utf8
 */
