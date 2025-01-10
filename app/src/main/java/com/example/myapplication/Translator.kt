package com.example.myapplication

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import java.io.InputStream

class Translator(context: Context) {

    private var translate: Translate

    init {
        try {
            // assets フォルダからサービスアカウント JSON を読み込む
            val inputStream: InputStream = context.assets.open("hacku-445214-0348cb79c9ba.json")

            // 認証情報を設定
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-translation"))

            // Translate API を初期化
            translate = TranslateOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .service
        } catch (e: Exception) {
            throw RuntimeException("Google Translate API の初期化に失敗しました: ${e.message}", e)
        }
    }

    /**
     * 翻訳を実行
     * @param text 翻訳したいテキスト
     * @param targetLanguage 翻訳先の言語（デフォルト: 日本語 "ja"）
     * @return 翻訳されたテキスト
     */
    fun translate(text: String, targetLanguage: String = "ja"): String? {
        return try {
            val translation: Translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage)
            )
            translation.translatedText
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
