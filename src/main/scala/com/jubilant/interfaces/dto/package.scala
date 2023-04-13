package com.jubilant.interfaces

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

package object dto {

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

//  implicit def localDateTimeFormat: Writes[LocalDateTime] =
//    temporalWrites[LocalDateTime, DateTimeFormatter](dateTimeFormatter)
//
//  implicit val DefaultLocalDateTimeReads: Reads[LocalDateTime] = localDateTimeReads(dateTimeFormatter)

}
