/*
 * File: $HeadURL: https://hdt-java.googlecode.com/svn/trunk/hdt-java/src/org/rdfhdt/hdt/util/string/CharSequenceComparator.java $
 * Revision: $Rev: 200 $
 * Last modified: $Date: 2013-04-17 23:36:44 +0100 (mi, 17 abr 2013) $
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
 */

package org.rdfhdt.hdt.util.string;

import org.rdfhdt.hdt.util.LiteralsUtils;

import java.util.Comparator;

/**
 * @author mario.arias
 *
 */
public final class CharSequenceCustomComparator implements Comparator<CharSequence> {

	private static final Comparator<CharSequence> instance = new CharSequenceCustomComparator();

	public static Comparator<CharSequence> getInstance() {
		return instance;
	}
		
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CharSequence s1, CharSequence s2) {
		if (s1 == s2) {
			return 0;
		}
		String type1 = LiteralsUtils.getType(s1);
		String type2 = LiteralsUtils.getType(s2);
		int x = type1.compareTo(type2);
		if (x != 0) {
			return x;
		} else { // data types are equal
			s1 = DelayedString.unwrap(s1);
			s2 = DelayedString.unwrap(s2);

			if (s1 instanceof CompactString && s2 instanceof CompactString) {
				CompactString cs1 = (CompactString) s1;
				CompactString cs2 = (CompactString) s2;
				return cs1.compareTo(cs2);
			}

			if (s1 instanceof String && s2 instanceof String) {
				String rs1 = (String) s1;
				String rs2 = (String) s2;
				return rs1.compareTo(rs2);
			}

			if (s1 instanceof ReplazableString && s2 instanceof ReplazableString) {
				ReplazableString cs1 = (ReplazableString) s1;
				ReplazableString cs2 = (ReplazableString) s2;
				return cs1.compareTo(cs2);
			}

			// Slower but safe

			return s1.toString().compareTo(s2.toString());
		}
	}

}
