package org.dbpedia.spotlight.io.feedback

import java.io.{FileWriter, PrintWriter, OutputStream, Writer, File}
import org.dbpedia.spotlight.model.SpotlightFeedback

/**
 * Store Spotlight Feedback
 *
 * @author pablomendes
 * @author Alexandre Cançado Cardoso - accardoso
 */

trait FeedbackStore {
  /* Add (append) the new feedback at the end of the storing file/database using the specific format */
  def add(feedback: SpotlightFeedback)
}

/**
 * Stores the feedback in a TSV file
 *
 * @constructor (output: Writer) -> Main constructor, receive a Writer where will append all new feedback.
 * @constructor (stream: OutputStream) -> receive a OutputStream and pass it into a Writer.
 * @constructor (file: File) -> receive a File and pass it into a Writer.
 * @constructor (storageFolderPath: String , storageFileName: String) -> create a new File named as informed at storageFileName in the paht informed at storageFolderPath, and pass it into a Writer.
 * @constructor (storageFolderPath: String) -> create a new File with the default name ("feedbackStore") in the path informed at storageFolderPath, and pass it into a Writer.
 */
class TSVFeedbackStore(output: Writer) extends FeedbackStore {
  /* Constructor with a OutputStream as output and converting it to a Writer */
  def this(stream: OutputStream) = this(new PrintWriter(stream))
  /* Constructor with a informed File as output and converting it to a appendable FileWriter */
  def this(file: File) = this(new FileWriter(file, true))
  /* Constructor with new File as output and convert it to a appendable FileWriter */
  def this(storageFolderPath: String, storageFileName: String) =
    this(new FileWriter(new File(storageFolderPath + File.separator + storageFileName + ".tsv"), true))
  /* Constructor with default storage file name */
  def this(storageFolderPath: String) = this(storageFolderPath, "feedbackStore")

  /* Add (append) the new feedback at the end of the storing file/database using the tsv format */
  def add(feedback: SpotlightFeedback) = {
    output.append("\n")
    output.append(feedback.mkString("\t"))
    output.flush()
  }

}

/**
 * Stores the feedback in a CSV file
 *
 * @constructor output: Writer -> Main constructor, receive a Writer where will append all new feedback.
 * @constructor stream: OutputStream -> receive a OutputStream and pass it into a Writer.
 * @constructor file: File -> receive a File and pass it into a Writer.
 * @constructor storageFolderPath: String , storageFileName: String -> create a new File named as informed at storageFileName in the paht informed at storageFolderPath, and pass it into a Writer.
 * @constructor storageFolderPath: String -> create a new File with the default name ("feedbackStore") in the path informed at storageFolderPath, and pass it into a Writer.
 */
class CSVFeedbackStore(output: Writer) extends FeedbackStore {
  /* Constructor with a OutputStream as output and converting it to a Writer */
  def this(stream: OutputStream) = this(new PrintWriter(stream))
  /* Constructor with a informed File as output and converting it to a appendable FileWriter */
  def this(file: File) = this(new FileWriter(file, true))
  /* Constructor with new File as output and convert it to a appendable FileWriter */
  def this(storageFolderPath: String, storageFileName: String) =
    this(new FileWriter(new File(storageFolderPath + File.separator + storageFileName + ".csv"), true))
  /* Constructor with default storage file name */
  def this(storageFolderPath: String) = this(storageFolderPath, "feedbackStore")

  /* Add (append) the new feedback at the end of the storing file/database using the csv format */
  def add(feedback: SpotlightFeedback) = {
    output.append("\n")
    output.append(feedback.mkString(","))
    output.flush
  }
}