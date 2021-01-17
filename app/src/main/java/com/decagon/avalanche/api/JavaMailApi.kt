package com.decagon.avalanche.api

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.decagon.avalanche.utils.Utils
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class JavaMailApi: AsyncTask<Void, Void, Void>() {

    //Variables
    private var mContext: Context? = null
    private var mSession: Session? = null

    private var mEmail: String? = null
    private var mSubject: String? = null
    private var mMessage: String? = null

    private var mProgressDialog: ProgressDialog? = null

    //Constructor
    fun JavaMailAPI(mContext: Context?, mEmail: String?, mSubject: String?, mMessage: String?) {
        this.mContext = mContext
        this.mEmail = mEmail
        this.mSubject = mSubject
        this.mMessage = mMessage
    }

    protected override fun onPreExecute() {
        super.onPreExecute()
        //Show progress dialog while sending email
        mProgressDialog =
            ProgressDialog.show(mContext, "Sending message", "Please wait...", false, false)
    }

    protected override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        //Dismiss progress dialog when message successfully send
        mProgressDialog!!.dismiss()

        //Show success toast
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show()
    }

    protected override fun doInBackground(vararg params: Void?): Void? {
        //Creating properties
        val props = Properties()

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")

        //Creating a new session
        mSession = Session.getDefaultInstance(props,
            object : Authenticator() {
                //Authenticating the password
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD)
                }
            })
        try {
            //Creating MimeMessage object
            val mm = MimeMessage(mSession)

            //Setting sender address
            mm.setFrom(InternetAddress(Utils.EMAIL))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(mEmail))
            //Adding subject
            mm.subject = mSubject
            //Adding message
            mm.setText(mMessage)
            //Sending email
            Transport.send(mm)

//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            messageBodyPart.setText(message);
//
//            Multipart multipart = new MimeMultipart();
//
//            multipart.addBodyPart(messageBodyPart);
//
//            messageBodyPart = new MimeBodyPart();
//
//            DataSource source = new FileDataSource(filePath);
//
//            messageBodyPart.setDataHandler(new DataHandler(source));
//
//            messageBodyPart.setFileName(filePath);
//
//            multipart.addBodyPart(messageBodyPart);

//            mm.setContent(multipart);
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}