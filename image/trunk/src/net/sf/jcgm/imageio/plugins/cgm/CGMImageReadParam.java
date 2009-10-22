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

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageReadParam;

import net.sf.jcgm.core.Message;

/**
 * CGM specific image read parameters. Adds the ability to define the resolution
 * to use for the CGM image, in DPI (dots per inch).
 * 
 * @author xphc (Philippe Cadé)
 * @version $Id$
 */
public class CGMImageReadParam extends ImageReadParam {
	/** The DPI setting to use, defaults to 96 DPI */
	private double dpi = 96.0;
	
	/** Error messages during the reading of the CGM file */
	private List<Message> messages = new ArrayList<Message>();
	
	/**
	 * Builds image read parameters for a CGM file, using the default DPI settings
	 */
	public CGMImageReadParam() {
		// empty
	}

	/**
	 * Builds image read parameters for a CGM file
	 * @param dpi The DPI setting to use
	 */
	public CGMImageReadParam(double dpi) {
		this.dpi = dpi;
	}

	/**
	 * Defines the dot per inch setting to use when reading the CGM file
	 * 
	 * @param dpi
	 *            the DPI to set
	 */
	public void setDPI(double dpi) {
		this.dpi = dpi;
	}

	/**
	 * Returns the dots per inch setting defined when reading the CGM file
	 * 
	 * @return the current DPI setting
	 */
	public double getDPI() {
		return this.dpi;
	}

	/**
	 * Returns the error messages that were encountered while reading the CGM
	 * files
	 * 
	 * @return A list of messages
	 */
	public List<Message> getMessages() {
		return this.messages;
	}

	/**
	 * Sets the error messages that were encountered during the reading of the
	 * CGM file. This method is called by the {@link CGMImageReader} class.
	 * 
	 * @param messages
	 *            The error messages to set
	 */
	public void setMessages(List<Message> messages) {
		this.messages.addAll(messages);
	}
}

/*
 * vim:encoding=utf8
 */
