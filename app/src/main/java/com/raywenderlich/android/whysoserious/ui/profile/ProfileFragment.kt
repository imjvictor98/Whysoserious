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

package com.raywenderlich.android.whysoserious.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.raywenderlich.android.whysoserious.R
import com.raywenderlich.android.whysoserious.common.onClick
import com.raywenderlich.android.whysoserious.profilePresenter
import com.raywenderlich.android.whysoserious.ui.welcome.WelcomeActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), ProfileView {

  private val presenter by lazy { profilePresenter() }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_profile, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.setView(this)
    initUi()
  }

  private fun initUi() {
    logoutButton.onClick { presenter.logOut() }
  }

  override fun showUsername(username: String) {
    userName.text = getString(R.string.username_text, username)
  }

  override fun showEmail(email: String) {
    userEmail.text = getString(R.string.email_text, email)
  }

  override fun showNumberOfJokes(jokesCount: Int) {
    numberOfJokes.text = getString(R.string.number_of_jokes_text, jokesCount)
  }

  override fun openWelcome() {
    startActivity(Intent(activity, WelcomeActivity::class.java))
    activity?.finish()
  }
}