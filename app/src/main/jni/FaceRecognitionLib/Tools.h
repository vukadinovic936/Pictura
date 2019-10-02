/*
 * Inspired by --->  Copyright (c) 2012. Philipp Wagner <bytefish[at]gmx[dot]de>.
 * Released to public domain under terms of the BSD Simplified license.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the organization nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *   See <http://www.opensource.org/licenses/bsd-license>
 */
 //edited by Miskewolf
#ifndef __tools_h__
#define __tools_h__

#include <numeric>
#include <vector>

using namespace std;

template<typename VectorType>
vector<size_t> sortIndexes(const VectorType &v) {
    // Based on: http://stackoverflow.com/a/12399290/2175837
    // Initialize original index locations
    vector<size_t> idx(v.size());
    for (size_t i = 0; i != idx.size(); ++i) idx[i] = i;
	
	for(size_t c = 0;c < ( idx.size() - 1 );c++) {
			for(size_t d=c+1;d<idx.size();d++) {
				if(v[d]<v[c]) {
					int save=idx[c];
					idx[c]=idx[d];
					idx[d]=save;
				}
				
			}
		}


    return idx;
};

#endif