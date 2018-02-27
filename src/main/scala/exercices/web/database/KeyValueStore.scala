package exercices.web.database

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Random

trait KeyValueStore[K, V] {
  /**
    * Get all available keys in store
    *
    * @return
    */
  def keys(): Future[Seq[K]]

  /**
    * Optionally get a value from the store
    *
    * @param key of the wanted value
    * @return
    */
  def read(key: K): Future[Option[V]]

  /**
    * Create a value in the store. It will fail if the key already exists
    *
    * @param key
    * @param value
    * @return
    */
  def create(key: K, value: V): Future[Int]

  /**
    * Update a value in the store. It will fail if the key does not exist
    *
    * @param key
    * @param value
    * @return
    */
  def update(key: K, value: V): Future[Int]

  /**
    * Delete a value in the store.
    *
    * @param key
    * @return if the key were present or not
    */
  def delete(key: K): Future[Boolean]

  /**
    * Set a value in the store. It will always work
    *
    * @param key
    * @param value
    * @return if the value was previously in the store
    */
  def createOrUpdate(key: K, value: V): Future[Boolean]
}

/**
  * @param failureRate between 0 and 1
  * @tparam K
  * @tparam V
  */
class InMemoryKeyValueStore[K, V](failureRate: Double) extends KeyValueStore[K, V] {
  private val store = mutable.Map[K, V]()

  private def simulateNetwork[T](v: T): Future[T] =
    if (Random.nextInt(1000) < failureRate * 1000) Future.failed(new Exception("network error"))
    else Future.successful(v)

  def keys(): Future[Seq[K]] =
    simulateNetwork(store.keys.toSeq)

  def read(key: K): Future[Option[V]] =
    simulateNetwork(store.get(key))

  def create(key: K, value: V): Future[Int] = {
    if (store.contains(key)) {
      Future.failed(new Exception(s"Key $key already exists"))
    } else {
      store.update(key, value)
      simulateNetwork(1)
    }
  }

  def update(key: K, value: V): Future[Int] = {
    if (store.contains(key)) {
      store.update(key, value)
      simulateNetwork(1)
    } else {
      Future.failed(new Exception(s"Key $key does not exist"))
    }
  }

  def delete(key: K): Future[Boolean] = {
    val res = store.contains(key)
    store.remove(key)
    simulateNetwork(res)
  }

  def createOrUpdate(key: K, value: V): Future[Boolean] = {
    val res = store.contains(key)
    store.update(key, value)
    simulateNetwork(res)
  }
}
