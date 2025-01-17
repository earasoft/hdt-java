/*
 * File: $HeadURL: https://hdt-java.googlecode.com/svn/trunk/hdt-java/src/org/rdfhdt/hdt/dictionary/impl/section/HashDictionarySection.java $
 * Revision: $Rev: 191 $
 * Last modified: $Date: 2013-03-03 11:41:43 +0000 (dom, 03 mar 2013) $
 * Last modified by: $Author: mario.arias $
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contacting the authors:
 *   Mario Arias:               mario.arias@deri.org
 *   Javier D. Fernandez:       jfergar@infor.uva.es
 *   Miguel A. Martinez-Prieto: migumar2@infor.uva.es
 *   Alejandro Andres:          fuzzy.alej@gmail.com
 */

package org.rdfhdt.hdt.dictionary.impl.section;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.rdfhdt.hdt.dictionary.TempDictionarySection;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.util.LiteralsUtils;
import org.rdfhdt.hdt.util.string.CharSequenceComparator;
import org.rdfhdt.hdt.util.string.CharSequenceCustomComparator;
import org.rdfhdt.hdt.util.string.CompactString;

/**
 * @author mario.arias
 *
 */
public class HashDictionarySection implements TempDictionarySection {
	public static final int TYPE_INDEX = 1;

	private HashMap<CharSequence, Long> map;
	private List<CharSequence> list;
	private int size;
	public boolean sorted;
	boolean isCustom;
	private HashMap<String,Long> literalsCounts;
	/**
	 *
	 */
	public HashDictionarySection(boolean isCustom) {
		this(new HDTSpecification());
		this.isCustom = isCustom;
	}
	public HashDictionarySection() {
		this(new HDTSpecification());
		this.isCustom = isCustom;
	}
	public HashDictionarySection(HDTOptions spec) {
		map = new HashMap<>();
		list = new ArrayList<>();
		size=0;
		literalsCounts = new HashMap<>();
	}

	/* (non-Javadoc)
	 * @see hdt.dictionary.DictionarySection#locate(java.lang.CharSequence)
	 */
	@Override
	public long locate(CharSequence s) {
		CompactString compact = new CompactString(s);
		Long val = map.get(compact);
		if(val==null) {
			return 0;
		}
		return val;
	}

	/* (non-Javadoc)
	 * @see hdt.dictionary.DictionarySection#extract(int)
	 */
	@Override
	public CharSequence extract(long pos) {
		if(pos<=0) {
			return null;
		}
		return list.get((int) (pos-1));
	}

	/* (non-Javadoc)
	 * @see hdt.dictionary.DictionarySection#size()
	 */
	@Override
	public long size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see hdt.dictionary.DictionarySection#getNumberOfElements()
	 */
	@Override
	public long getNumberOfElements() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see hdt.dictionary.DictionarySection#getEntries()
	 */
	@Override
	public Iterator<? extends CharSequence> getSortedEntries() {
		if(!sorted) {
			return null;
		}
		return list.iterator();
	}

	@Override
	public Iterator<? extends CharSequence> getEntries() {
		return list.iterator();
	}

	@Override
	public long add(CharSequence entry) {
		CharSequence compact = new CompactString(entry);
		Long pos = map.get(compact);
		if(pos!=null) {
			// Found return existing ID.
			return pos;
		}

		// Not found, insert new
		list.add(compact);
		map.put(compact, (long) list.size());

		size+=compact.length();
		sorted = false;

		// custom for subsection literals ..
		if(isCustom){
			String type = LiteralsUtils.getType(entry);
			// check if the entry doesn't already exists
			if(map.get(entry) == null) {
				if (literalsCounts.containsKey(type)) {
					literalsCounts.put(type, literalsCounts.get(type) + 1L);
				} else
					literalsCounts.put(type, 1L);
			}
		}
		return list.size();
	}

	@Override
	public void remove(CharSequence seq) {
		map.remove(seq);
		sorted = false;
	}

	@Override
	public void sort() {
		// Update list.
		list = new ArrayList<>(map.size());
		for(CharSequence str : map.keySet()) {
			list.add(str);
		}

		// Sort list
		if(isCustom)
			Collections.sort(list, new CharSequenceCustomComparator());
		else
			Collections.sort(list, new CharSequenceComparator());

		// Update map indexes
		for(long i=1;i<=getNumberOfElements();i++) {
			map.put(extract(i), i);
		}

		sorted = true;
	}

	@Override
	public boolean isSorted() {
		return sorted;
	}

	@Override
	public void clear() {
		list.clear();
		map.clear();
		size=0;
		sorted = false; //because if sorted won't be anymore
	}

	@Override
	public void close() throws IOException {
		map=null;
		list=null;
	}

	public HashMap<String, Long> getLiteralsCounts() {
		return literalsCounts;
	}
}
