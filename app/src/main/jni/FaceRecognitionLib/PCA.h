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
 *///edited by Miskewolf
#ifndef __pca_h__
#define __pca_h__

#include <Eigen/Dense> // http://eigen.tuxfamily.org

using namespace Eigen;

class PCA {
public:
    /**
     * Computes the Eigenvectors of the images using PCA.
     * @param  images        Each images is represented as a column vector.
     * @param  numComponents Number of singular values used. If this is set to -1, a cumulative energy threshold of 90 % is used.
     * @return               Returns the number of components used.
     */
    int32_t compute(const MatrixXi &images, int32_t numComponents = -1);

protected:
    MatrixXf U; // Eigenvectors
    VectorXf mu; // Mean along each row

private:
    const float cumulativeEnergyThreshold = .9f; // Determine the number of principal components required to model 90 % of data variance
};

#endif
