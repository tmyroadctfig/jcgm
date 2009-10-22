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
package net.sf.jcgm.image.loader.cgm;

import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;

import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.impl.imageio.ImageIOUtil;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;

/**
 * Preloader for CGM files.
 * <p>
 * Note: this class relies on the
 * {@link net.sf.jcgm.imageio.plugins.cgm.CGMImageReader} Image I/O plugin to
 * determine the image's size.
 * <p>
 * This implementation is largely inspired from the SVG Image preloader provided
 * with the XML graphics library.
 * 
 * @author xphc (Philippe Cadé)
 * @version $Id$
 * @since Apr 14, 2009
 * @see ImageLoaderCGM
 * @see <a href="http://xmlgraphics.apache.org/">xmlgraphics.apache.org</a>
 */
public class PreloaderCGM extends AbstractImagePreloader {

	@Override
	public ImageInfo preloadImage(String originalURI, Source src, ImageContext context)
			throws ImageException, IOException {
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(in);
        if (!iter.hasNext()) {
            return null;
        }
        
        IOException firstIOException = null;
        IIOMetadata iiometa = null;
        ImageSize size = null;
        String mime = null;
        while (iter.hasNext()) {
        	ImageReader reader = iter.next();
        	try {
        		reader.setInput(ImageUtil.ignoreFlushing(in), true, false);
        		in.mark();
                final int imageIndex = 0;
                iiometa = reader.getImageMetadata(imageIndex);
                size = new ImageSize();
                size.setSizeInPixels(reader.getWidth(imageIndex), reader.getHeight(imageIndex));
                mime = reader.getOriginatingProvider().getMIMETypes()[0];
                break;
            } catch (IOException ioe) {
                //remember the first exception, ignore all others and continue
                if (firstIOException == null) {
                    firstIOException = ioe;
                }
            } finally {
                reader.dispose();
                in.reset();
            }
        }
        
        if (iiometa == null || size == null) {
            if (firstIOException == null) {
                throw new ImageException("Could not extract image metadata");
            } else {
                throw new ImageException("I/O error while extracting image metadata"
                        + (firstIOException.getMessage() != null
                            ? ": " + firstIOException.getMessage()
                            : ""),
                        firstIOException);
            }
        }
        
        //Resolution (first a default, then try to read the metadata)
        size.setResolution(context.getSourceResolution());
        ImageIOUtil.extractResolution(iiometa, size);
        if (size.getWidthMpt() == 0) {
            size.calcSizeFromPixels();
        }
        
        ImageInfo info = new ImageInfo(originalURI, mime);
        info.getCustomObjects().put(ImageIOUtil.IMAGEIO_METADATA, iiometa);
        // remember the input stream that will be used in the loader
        info.getCustomObjects().put("InputStream", in);
        info.setSize(size);

        return info;
	}

}
