# HACKU App (My Application)

カメラで撮影した物体をAIでリアルタイムに認識し、その名前の翻訳や画像へのデコレーションを行えるAndroidアプリケーションです。

## 🚀 主な機能

1. **AI物体認識**
   - TensorFlow Liteを使用して、カメラに写った物体を識別します。
   - 認識された物体はバウンディングボックスで囲まれ、クラス名と信頼度が表示されます。

2. **多言語翻訳**
   - Google Cloud Translation APIを連携し、認識された英語のラベルを即座に日本語に翻訳します。

3. **画像デコレーション**
   - 撮影した画像に対して、タップ操作で星やハートなどのスタンプを自由に追加できます。

## 📸 認識可能なオブジェクト
以下の26種類のオブジェクトを認識するように設計されています：
- モニター、キーボード、デスク、観葉植物、椅子、ノートパソコン、ボールペン、扇風機、時計、ティッシュ、ゴミ箱、エアコン、腕時計、新聞、本、カレンダー、トイレ、リモコン、段ボール、消しゴム、シャープペンシル、ホッチキス、鉛筆、ペットボトル、木

## 🛠 技術スタック
- **Language**: Kotlin
- **Architecture**: Jetpack (CameraX, ViewBinding)
- **AI/ML**: TensorFlow Lite
- **API**: Google Cloud Translation API
- **License**: GNU Affero General Public License v3.0

## 🛠 セットアップと要件

### 必要な設定
- **APIキー**: 翻訳機能を利用するには、Google Cloudのサービスアカウントキー（JSON）を `app/src/main/assets/` フォルダに配置する必要があります。
  - ファイル名: `hacku-445214-0348cb79c9ba.json` (※セキュリティのため、公開リポジトリに含める際はご注意ください)

### 実行環境
- Android 7.0 (APIレベル 24) 以上
- カメラ機能付きのAndroidデバイス

## 📄 ライセンス
このプロジェクトは **GNU Affero General Public License v3.0** のもとで公開されています。詳細は [LICENSE.txt](./LICENSE.txt) をご覧ください。
