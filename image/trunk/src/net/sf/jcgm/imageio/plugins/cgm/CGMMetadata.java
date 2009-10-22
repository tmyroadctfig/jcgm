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
import java.util.Iterator;
import java.util.List;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

/**
 * FIXME: This class is not really implemented, most is just a placeholder.
 * 
 * @author xphc (Philippe Cadé)
 * @version $Id$
 * @since Mar 17, 2009
 */
class CGMMetadata extends IIOMetadata {

	static final boolean standardMetadataFormatSupported = false;
	static final String nativeMetadataFormatName =
		"net.sf.jcgm.imageio.plugins.cgm.CGMMetadata_1.0";
	static final String nativeMetadataFormatClassName =
		"net.sf.jcgm.imageio.plugins.cgm.CGMMetadata";
	static final String[] extraMetadataFormatNames = null;
	static final String[] extraMetadataFormatClassNames = null;

	// Keyword/value pairs
	List<String> keywords = new ArrayList<String>();
	List<String> values = new ArrayList<String>();

	/**
	 * 
	 */
	public CGMMetadata() {
		super(standardMetadataFormatSupported, nativeMetadataFormatName,
			nativeMetadataFormatClassName, extraMetadataFormatNames,
			extraMetadataFormatClassNames);
	}

	/**
	 * @param standardMetadataFormatSupported
	 * @param nativeMetadataFormatName
	 * @param nativeMetadataFormatClassName
	 * @param extraMetadataFormatNames
	 * @param extraMetadataFormatClassNames
	 */
	public CGMMetadata(boolean standardMetadataFormatSupported, String nativeMetadataFormatName,
			String nativeMetadataFormatClassName, String[] extraMetadataFormatNames,
			String[] extraMetadataFormatClassNames) {
		super(standardMetadataFormatSupported, nativeMetadataFormatName,
				nativeMetadataFormatClassName, extraMetadataFormatNames,
				extraMetadataFormatClassNames);
	}

	@Override
	public Node getAsTree(String formatName) {
		if (!formatName.equals(nativeMetadataFormatName)) {
			throw new IllegalArgumentException("Bad format name!");
		}
		
		// Create a root node
		IIOMetadataNode root =
			new IIOMetadataNode(nativeMetadataFormatName);

		// Add a child to the root node for each keyword/value pair
		Iterator<String> keywordIter = this.keywords.iterator();
		Iterator<String> valueIter = this.values.iterator();
		while (keywordIter.hasNext()) {
			IIOMetadataNode node =
				new IIOMetadataNode("KeywordValuePair");
			node.setAttribute("keyword", keywordIter.next());
			node.setAttribute("value", valueIter.next());
			root.appendChild(node);
		}

		return root;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void mergeTree(String formatName, Node root) {
		// unimplemented for the reader
	}

	@Override
	public void reset() {
		// unimplemented for the reader
	}

}

/*
 * vim:encoding=utf8
 */
