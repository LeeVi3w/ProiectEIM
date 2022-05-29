package com.example.proiecteim

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class HttpsTrustManager : X509TrustManager {
    private val _AcceptedIssuers : Array<X509Certificate> = arrayOf()

    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
    }

    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return _AcceptedIssuers
    }

    fun isClientTrusted(chain: Array<out X509Certificate>?) : Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<out X509Certificate>?) : Boolean {
        return true
    }

    companion object {
        private var trustManagers : Array<out TrustManager>? = null

        fun allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {
                override fun verify(p0: String?, p1: SSLSession?): Boolean {
                    return true
                }
            })

            var context : SSLContext? = null
            if (trustManagers == null) {
                trustManagers = arrayOf(HttpsTrustManager())
            }

            try {
                context = SSLContext.getInstance("TLS")
                context.init(null, trustManagers, SecureRandom())
            } catch (e : NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e : KeyManagementException) {
                e.printStackTrace()
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(context!!.socketFactory)
        }
    }
}