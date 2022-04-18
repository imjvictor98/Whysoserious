/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.whysoserious.firebase.database

import com.google.firebase.database.*
import com.raywenderlich.android.whysoserious.model.*
import javax.inject.Inject

private const val KEY_USER = "user"
private const val KEY_JOKE = "joke"
private const val KEY_FAVORITE = "favorite"

class FirebaseDatabaseManager
@Inject constructor(private val database: FirebaseDatabase) : FirebaseDatabaseInterface {

  override fun createUser(id: String, name: String, email: String) {
    val user = User(id, name, email)
    database
      .reference
      .child(KEY_USER)
      .child(id)
      .setValue(user)
  }

  override fun listenToJokes(onResult: (Joke) -> Unit) {
    database
      .reference
      .child(KEY_JOKE)
      .orderByKey()
      .addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot : DataSnapshot, previousChildName : String?) {
          snapshot.getValue(JokeResponse::class.java)?.run {
             if (isValid()) {
               onResult(mapToJoke())
             }
          }
        }

        override fun onChildChanged(snapshot : DataSnapshot, previousChildName : String?) = Unit

        override fun onChildRemoved(snapshot : DataSnapshot) = Unit

        override fun onChildMoved(snapshot : DataSnapshot, previousChildName : String?) = Unit

        override fun onCancelled(error : DatabaseError) = Unit

      })
  }

  override fun addNewJoke(joke: Joke, onResult: (Boolean) -> Unit) {
    val newJokeReference = database.reference.child(KEY_JOKE).push()
    val newJoke = newJokeReference.key?.let { joke.copy(id = it) }

    newJokeReference.setValue(newJoke).addOnCompleteListener {
      onResult(it.isSuccessful && it.isComplete)
    }
  }

  override fun getFavoriteJokes(userId: String, onResult: (List<Joke>) -> Unit) {
    database
      .reference
      .child(KEY_USER)
      .child(userId)
      .child(KEY_FAVORITE)
      .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot : DataSnapshot) {
          snapshot.run {
            val jokes = children.mapNotNull {
              it.getValue(JokeResponse::class.java)
            }
            onResult(jokes.map(JokeResponse::mapToJoke))
          }
        }

        override fun onCancelled(error : DatabaseError) = onResult(listOf())
      })
  }

  override fun changeJokeFavoriteStatus(joke: Joke, userId: String) {
    val reference = database
      .reference
      .child(KEY_USER)
      .child(userId)
      .child(KEY_FAVORITE)
      .child(joke.id)

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(snapshot : DataSnapshot) {
        val oldJoke = snapshot.getValue(JokeResponse::class.java)

        oldJoke?.let {
          reference.setValue(null)
        } ?: run {
          reference.setValue(joke)
        }
      }

      override fun onCancelled(error : DatabaseError) = Unit
    })
  }

  override fun getProfile(id: String, onResult: (User) -> Unit) {
    database
      .reference
      .child(KEY_USER)
      .child(id)
      .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot : DataSnapshot) {
          val user = snapshot.getValue(UserResponse::class.java)
          val favoriteJokes = snapshot.child(KEY_FAVORITE).children
            .map { it?.getValue(JokeResponse::class.java)}
            .mapNotNull { it?.mapToJoke() }
          user?.run {
            onResult(User(id, username, email, favoriteJokes))
          }
        }

        override fun onCancelled(error : DatabaseError) = Unit

      })
  }
}