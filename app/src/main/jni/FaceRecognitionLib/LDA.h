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
#ifndef __lda_h__
#define __lda_h__

#include <Eigen/Dense> // http://eigen.tuxfamily.org

using namespace Eigen;

class LDA {
public:
    /**
     * Computes the Eigenvectors of X using LDA.
     * @param  X             Data matrix.
     * @param  classes       Class vector. Must start at 1 and increment by one.
     * @param  numComponents Number of components used for the analysis.
     * @return               Returns the number of components used.
     */
    int32_t compute(const MatrixXf &X, const VectorXi &classes, int32_t numComponents);

protected:
    MatrixXf U; // Eigenvectors
};

#endif
