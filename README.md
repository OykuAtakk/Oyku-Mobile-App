# 📸 Öykü

**Firebase tabanlı fotoğraf paylaşım & gerçek‑zamanlı sohbet uygulaması**  
Java • ViewBinding • Material Components

> “Öykü, anılarınızı yakalayıp arkadaşlarınızla gerçek‑zamanlı olarak paylaşmanın en hızlı yolu.” 🚀

---

## ✨ Özellikler

| Modül | Açıklama |
|-------|----------|
| **Kullanıcı Doğrulama** | Firebase Authentication ile e‑posta/şifre kullanarak kayıt & giriş |
| **Gönderi Paylaşımı** | Galeriden fotoğraf seçimi → Firebase Storage’a yükleme → Cloud Firestore'a başlık & açıklama ile kayıt |
| **Akış (Feed)** | Tüm gönderileri listeleyen `RecyclerView` + Glide ile görsel önbellekleme |
| **Gerçek‑Zamanlı Sohbet** | Kullanıcılar arası kanal mantığında Cloud Firestore tabanlı anlık mesajlaşma |
| **Mesaj İstekleri** | Sohbet öncesinde isteğe bağlı kanal oluşturma ve bildirim rozeti |
| **Profil** | Kullanıcı bilgilerini görüntüleme & oturum kapatma |
| **Alt Navigasyon** | `BottomNavigationView` ile Feed, Kullanıcılar, Mesajlar ve Profil geçişi |

---

## 🏗️ Mimari & Teknolojiler

| Katman | Teknoloji |
|--------|-----------|
| **Backend** | Firebase Authentication · Cloud Firestore · Cloud Storage |
| **UI** | AndroidX · Material Components · ViewBinding |
| **Görsel Yükleme** | Glide (Feed) · Picasso (Chat & Profil) |
| **Diğer** | CircleImageView · BottomNavigationView · DialogFragment |

<details>
<summary>📁 Klasör Yapısı (özet)</summary>

```text
app/
 └─ src/main/java/com/oykuatak/oyku/
     ├─ activity/            # Activity sınıfları
     │   ├─ ChatActivity.java
     │   ├─ FeedActivity.java
     │   ├─ MainActivity.java
     │   ├─ SignUpActivity.java
     │   └─ UploadActivity.java
     ├─ adapter/             # RecyclerView adapter'ları
     │   ├─ BlogAdapter.java
     │   ├─ ChatAdapter.java
     │   ├─ MessageRequestsAdapter.java
     │   └─ UserAdapter.java
     ├─ fragment/            # UI fragment'ları
     │   ├─ FeedFragment.java
     │   ├─ MessageFragment.java
     │   ├─ ProfileFragment.java
     │   └─ UsersFragment.java
     └─ model/               # Veri modelleri
         ├─ Blog.java
         ├─ Chat.java
         ├─ MessageRequest.java
         └─ User.java

## ⚙️ Kurulum

1. Depoyu klonlayın:

   ```bash
   git clone https://github.com/<kullanıcı-adı>/oyku.git
   cd oyku
Android Studio Arctic Fox (veya üzeri) ile projeyi açın.

Firebase yapılandırması

Firebase Console’da yeni bir proje oluşturun.

Android paket adı: com.oykuatak.oyku

İndirilen google-services.json dosyasını app/ klasörüne kopyalayın.

Authentication → Sign‑in Method bölümünde “E‑posta/Şifre” yöntemini etkinleştirin.

Cloud Firestore ve Storage’ı etkinleştirin.

Gradle’ı senkronize edin ve projeyi derleyin.

## ▶️ Projeyi Çalıştırma

| Adım | Açıklama |
|------|----------|
| **Kayıt / Giriş** | `MainActivity` & `SignUpActivity` üzerinden kullanıcı oluşturun veya mevcut hesapla oturum açın. |
| **Gönderi Paylaş** | Sağ üst menüden **Upload** seçeneğiyle `UploadActivity`’de fotoğraf yükleyin. |
| **Akışta Görüntüle** | Gönderiniz otomatik olarak Feed’e düşer. |
| **Kullanıcılar** | **People** sekmesinden diğer kullanıcıları listeleyin, mesaj isteği gönderin. |
| **Mesajlaşma** | İstek kabul edildiğinde `ChatActivity`’de gerçek‑zamanlı sohbet başlayacaktır. |
