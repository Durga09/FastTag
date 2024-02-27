package com.agent.fasttag.view.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object FasTagSharedPreference {


    val CUSTOM_PREF_NAME = "User_data"

        val USER_ID = "USER_ID"
        val USER_PASSWORD = "PASSWORD"
        val PAYMENT_BACK_URL = "PAYMENT_BACK_URL"
        val Login_USER_parentId = "Login_USER_parentId"
        val LOGIN_USER_USERNAME = "LOGIN_USER_USERNAME"
        val Login_USER_email ="Login_USER_email"
        val Login_USER_phoneNumber ="Login_USER_phoneNumber"
        val Login_USER_firstName ="Login_USER_firstName"
        val Login_USER_lastName ="Login_USER_lastName"
        val Login_USER_roleId ="Login_USER_roleId"
        val Login_USER_roleName ="Login_USER_roleName"
        val Login_USER_agentID ="Login_USER_agentID"

    fun defaultPreference(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        fun customPreference(context: Context, name: String): SharedPreferences =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)

        inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
            val editMe = edit()
            operation(editMe)

            editMe.apply()
        }
    inline fun SharedPreferences.clear(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.clear();
        editMe.commit();
        editMe.apply()
    }
        var SharedPreferences.userId
            get() = getInt(USER_ID, 0)
            set(value) {
                editMe {
                    it.putInt(USER_ID, value)
                }
            }

        var SharedPreferences.password
            get() = getString(USER_PASSWORD, "")
            set(value) {
                editMe {
                    it.putString(USER_PASSWORD, value)
                }
            }
      var SharedPreferences.USER_USERNAME
        get() = getString(LOGIN_USER_USERNAME, "")
        set(value) {
            editMe {
                it.putString(LOGIN_USER_USERNAME, value)
                it.commit()
            }
        }
    var SharedPreferences.PAYMENTBACKURL
        get() = getString(PAYMENT_BACK_URL, "")
        set(value) {
            editMe {
                it.putString(PAYMENT_BACK_URL, value)
                it.commit()
            }
        }
    var SharedPreferences.USER_agentID
        get() = getString(Login_USER_agentID, "")
        set(value) {
            editMe {
                it.putString(Login_USER_agentID, value)
            }
        }
    var SharedPreferences.USER_parentId
        get() = getString(Login_USER_parentId, "")
        set(value) {
            editMe {
                it.putString(Login_USER_parentId, value)
            }
        }
    var SharedPreferences.USER_email
        get() = getString(Login_USER_email, "")
        set(value) {
            editMe {
                it.putString(Login_USER_email, value)
            }
        }
    var SharedPreferences.USER_phoneNumber
        get() = getString(Login_USER_phoneNumber, "")
        set(value) {
            editMe {
                it.putString(Login_USER_phoneNumber, value)
            }
        }
    var SharedPreferences.USER_firstName
        get() = getString(Login_USER_firstName, "")
        set(value) {
            editMe {
                it.putString(Login_USER_firstName, value)
                it.commit()
            }
        }
    var SharedPreferences.USER_lastName
        get() = getString(Login_USER_lastName, "")
        set(value) {
            editMe {
                it.putString(Login_USER_lastName, value)
                it.commit()
            }
        }
    var SharedPreferences.USER_roleId
        get() = getString(Login_USER_roleId, "")
        set(value) {
            editMe {
                it.putString(Login_USER_roleId, value)
                it.commit()
            }
        }
    var SharedPreferences.USER_roleName
        get() = getString(Login_USER_roleName, "")
        set(value) {
            editMe {
                it.putString(Login_USER_roleName, value)
                it.commit()
            }
        }
        var SharedPreferences.clearValues
            get() = { }
            set(value) {
                editMe {
                    it.clear()
                }
            }


    }
