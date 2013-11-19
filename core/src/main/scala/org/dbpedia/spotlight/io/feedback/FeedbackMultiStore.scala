package org.dbpedia.spotlight.io.feedback

import java.io.File
import org.dbpedia.spotlight.model.SpotlightFeedback

/**
 * This class manage many feedback stores (implementations of FeedbackStore trait).
 * Allowing to add many stores (storage files, stream, or databases) and add a feedback to all of then with only one addFeedback call.
 *
 * @author Alexandre Cançado Cardoso - accardoso
 *
 * @constructor (stores: List[FeedbackStore]) -> Create a Multiple Store Manager and register a list o Stores
 * @constructor () -> Create a Multiple Store Manager without any registered Store
 */

class FeedbackMultiStore(var stores: List[FeedbackStore]) {
  def this() = this(List[FeedbackStore]())

  /* Add (Register) a new store (a FeedbackStore implementation - a storage file, stream, or database) to the multi store instance. */
  def registerStore(store: FeedbackStore) = {
    stores = stores :+ store
  }

  /* Add the received standard feedback to all registered stores */
  def storeFeedback(feedback: SpotlightFeedback) = {
    if(stores.isEmpty)
      throw new NullPointerException("Multi Store Manager has no registered store. Please, register at least one before store a feedback.")

    for (store <- stores){
      store.add(feedback)
    }
  }

  def storeFeedbackBatch(feedbackList: List[SpotlightFeedback]) = {
    if(stores.isEmpty)
      throw new NullPointerException("Multi Store Manager has no registered store. Please, register at least one before store a feedback.")

    for (store <- stores){
      store.addAll(feedbackList)
    }
  }

  def storeAllFeedbackIn(feedbackLoader: FeedbackLoader) = {
    if(stores.isEmpty)
      throw new NullPointerException("Multi Store Manager has no registered store. Please, register at least one before store a feedback.")

    for (store <- stores){
      store.convertFrom(feedbackLoader)
    }
  }

  def close() = stores.foreach(_.close())

  def forceClose() = stores.foreach(_.forceClose())

}


object FeedbackMultiStore {
  /* Create the folder where the files with the feedback will be placed */
  def createStorageFolder (storageFolderName: String) : String = {
    val warehouse = new File(storageFolderName)
    val created:Boolean = warehouse.mkdir()
    if (!created){
      if (warehouse.exists() && !warehouse.isDirectory)
        throw new IllegalAccessError("File exists and is not a directory.")
    }
    warehouse.getCanonicalPath
  }
}