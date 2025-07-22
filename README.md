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

<summary>📁 Klasör Yapısı (Özet)</summary>

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
```
---

## ⚙️ Kurulum

1. Depoyu klonlayın:
   git clone https://github.com/<kullanıcı-adı>/oyku.git
   cd oyku
2. Android Studio ile projeyi açın.

3. Firebase yapılandırması:

4. Firebase Console’da yeni bir proje oluşturun.

Android paket adı: com.oykuatak.oyku

google-services.json dosyasını app/ klasörüne kopyalayın.

5. Authentication → Sign-in Method altında “E‑posta/Şifre” yöntemini etkinleştirin.

6. Cloud Firestore ve Cloud Storage’ı etkinleştirin.

7. SHA‑1 / SHA‑256 imzalarını Firebase’e ekleyin (./gradlew signingReport komutu ile bulunabilir).

8. Gradle yapılandırmasını senkronize edin ve projeyi çalıştırın.



## ▶️ Projeyi Çalıştırma

| Adım | Açıklama |
|------|----------|
| **Kayıt / Giriş** | `MainActivity` & `SignUpActivity` üzerinden kullanıcı oluşturun veya mevcut hesapla oturum açın. |
| **Gönderi Paylaş** | Sağ üst menüden **Upload** seçeneğiyle `UploadActivity`’de fotoğraf yükleyin. |
| **Akışta Görüntüle** | Gönderiniz otomatik olarak Feed’e düşer. |
| **Kullanıcılar** | **People** sekmesinden diğer kullanıcıları listeleyin, mesaj isteği gönderin. |
| **Mesajlaşma** | İstek kabul edildiğinde `ChatActivity`’de gerçek‑zamanlı sohbet başlayacaktır. |
