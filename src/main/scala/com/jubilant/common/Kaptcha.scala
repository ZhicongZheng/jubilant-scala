package com.jubilant.common

import com.google.code.kaptcha.Constants._
import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config

import java.awt.image.BufferedImage
import java.util.Properties

object Kaptcha {

  def createText: String = defaultKaptcha.createText()

  def createImage(text: String): BufferedImage = defaultKaptcha.createImage(text)

  private val defaultKaptcha: DefaultKaptcha = buildInstance

  private def buildInstance = {
    val defaultKaptcha = new DefaultKaptcha
    val properties     = new Properties()
    // 是否有边框 默认为true 我们可以自己设置yes，no
    properties.setProperty(KAPTCHA_BORDER, "no")
    // 验证码文本字符颜色 默认为Color.BLACK
    properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black")
    // 验证码图片宽度 默认为200
    properties.setProperty(KAPTCHA_IMAGE_WIDTH, "120")
    // 验证码图片高度 默认为50
    properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "38")
    // 验证码文本字符大小 默认为40
    properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "30")
    // 验证码文本字符长度 默认为5
    properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4")
    // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
    properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier")
    // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
    properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.WaterRipple")
    val config = new Config(properties)
    defaultKaptcha.setConfig(config)
    defaultKaptcha
  }
}
