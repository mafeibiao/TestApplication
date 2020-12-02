//
// Created by feibiao.ma on 2020/10/16.
//

#include "image_render.h"
#include "../utils/logger.h"
#include "../filter/base/image_filter.h"
#include <malloc.h>
#include <math.h>
#include <cmath>

ImageRender::ImageRender(int origin_width, int origin_height, void *p) {
    m_origin_width = (float) origin_width;
    m_origin_height = (float) origin_height;
    cst_data = p;
    m_texture_id = OpenGLUtils::loadTexture(cst_data, m_origin_width, m_origin_height);
}

ImageRender::~ImageRender() {

}


void ImageRender::DoDraw() {
    if (isReadyToDraw()) {
        if (m_filter == NULL) {
            m_filter = new ImageFilter();
        }
        m_filter->OnInit();
        m_filter->onOutputSizeChanged(m_output_width, m_output_height);
        m_filter->DoDraw(m_texture_id, m_vertex_coors,
                         m_texture_coors);
    }

}


void ImageRender::AdjustImageScale() {
    if (m_output_width > 0
        && m_output_height > 0
        && m_origin_width > 0
        && m_origin_height > 0) {
        ResetTextureCoors();
        float ratio1 = m_output_width / m_origin_width;
        float ratio2 = m_output_height / m_origin_height;
        LOGE("MFB", "radio1:%f ratio2:%f", ratio1, ratio2)
        float ratioMax = fmaxf(ratio1, ratio2);
        int imageWidthNew = round(m_origin_width * ratioMax);
        int imageHeightNew = round(m_origin_height * ratioMax);

        float ratioWidth = imageWidthNew / m_output_width;
        float ratioHeight = imageHeightNew / m_output_height;

        float distHorizontal = (1 - 1 / ratioWidth) / 2;
        float distVertical = (1 - 1 / ratioHeight) / 2;
        m_texture_coors[0] = addDistance(m_texture_coors[0], distHorizontal);
        m_texture_coors[1] = addDistance(m_texture_coors[1], distVertical);
        m_texture_coors[2] = addDistance(m_texture_coors[2], distHorizontal);
        m_texture_coors[3] = addDistance(m_texture_coors[3], distVertical);
        m_texture_coors[4] = addDistance(m_texture_coors[4], distHorizontal);
        m_texture_coors[5] = addDistance(m_texture_coors[5], distVertical);
        m_texture_coors[6] = addDistance(m_texture_coors[6], distHorizontal);
        m_texture_coors[7] = addDistance(m_texture_coors[7], distVertical);

    }

}

void ImageRender::OnOutputSizeChanged(int outputWidth, int outputHeight) {
    m_output_width = (float) outputWidth;
    m_output_height = (float) outputHeight;
    AdjustImageScale();
}

float ImageRender::addDistance(const GLfloat coordinate, float distance) {
    return coordinate == 0.0f ? distance : 1 - distance;
}

void ImageRender::ResetTextureCoors() {
    m_texture_coors[0] = 0.0f;
    m_texture_coors[1] = 1.0f;
    m_texture_coors[2] = 1.0f;
    m_texture_coors[3] = 1.0f;
    m_texture_coors[4] = 0.0f;
    m_texture_coors[5] = 0.0f;
    m_texture_coors[6] = 1.0f;
    m_texture_coors[7] = 0.0f;
}

void ImageRender::Release() {
    if (m_filter != NULL) {
        m_filter->Release();
    }
}


void ImageRender::setFilter(ImageFilter *mFilter) {
    m_filter = mFilter;
}

ImageFilter *ImageRender::getFilter() const {
    return m_filter;
}

bool ImageRender::isReadyToDraw() {
    return m_origin_width > 0 && m_origin_height > 0;
}


