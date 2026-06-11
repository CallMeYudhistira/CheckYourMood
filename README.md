# CheckYourMood

## AI Mood Checker App (Android + Backend API)

CheckYourMood adalah aplikasi pendeteksi mood berbasis AI yang menganalisis ekspresi wajah melalui kamera dan memprediksi emosi secara *real-time*. Proyek ini dilengkapi dengan aplikasi Android (Java) sebagai *frontend* dan **dua pilihan arsitektur Backend API** yang bisa Anda gunakan sesuai kebutuhan.

Proyek ini dibuat tanpa database dan berfokus murni pada analisis ekspresi wajah yang cepat dan responsif.

---

## 🚀 Features

- Capture foto menggunakan kamera Android (mendukung resolusi tinggi)
- Deteksi emosi wajah menggunakan AI yang sangat akurat
- Tersedia 2 pilihan Backend: **Python (DeepFace)** dan **Node.js (face-api.js)**
- Mapping emosi ke Bahasa Indonesia beserta deskripsi interaktif
- *Loading indicator* saat proses analisis
- Simpan hasil (foto + teks mood) langsung ke Gallery HP
- Menggunakan *FileProvider* untuk *image handling* yang aman

Emosi yang didukung:
- Bahagia 😁
- Sedih 😔
- Marah 😡
- Terkejut 😲
- Takut 😰
- Jijik 🤢
- Netral 😑

---

## 🧠 Tech Stack

### Frontend (Android)
- Java (Android Studio)
- Camera Intent & FileProvider
- Volley (HTTP Request)
- MediaStore API

### Backend 1: Python (DeepFace)
- Python 3.8+
- Flask
- DeepFace & RetinaFace Backend
- TensorFlow / OpenCV

### Backend 2: Node.js (face-api.js)
- Node.js 18+
- Express.js
- face-api.js (SSD Mobilenet V1 & Face Expression Net)
- @tensorflow/tfjs (Tanpa AVX binding, aman untuk komputer/laptop lama)

---

## 📦 Pilihan Backend (Penting!)

Anda bisa memilih salah satu dari dua backend yang tersedia di dalam folder `backend/`:

1. **`backend/python`**: Sangat akurat menggunakan model *RetinaFace* milik DeepFace. Namun, **wajib** menggunakan CPU modern yang mendukung instruksi arsitektur **AVX**. Jika Anda menjalankannya di laptop tua, ini akan *crash* dengan kode error `132 (SIGILL)`.
2. **`backend/js`**: Menggunakan JavaScript murni dan WebAssembly. Memang sedikit lebih lambat 1-2 detik, namun **100% dijamin jalan di prosesor (CPU) jadul apa pun** tanpa error AVX, dengan tingkat akurasi tinggi berkat model *SSD Mobilenet V1*.

---

## ⚙️ Cara Menjalankan (menggunakan Docker - Paling Mudah)

Sangat disarankan menjalankan API menggunakan Docker.

1. Buka file `docker-compose.yml` di folder utama.
2. Cari baris `build:` di bawah `emotion-api`.
3. Arahkan ke backend yang ingin Anda pakai:
   - Untuk Python: `build: ./backend/python`
   - Untuk Node.js: `build: ./backend/js`
4. Buka terminal dan jalankan:
   ```bash
   docker-compose up --build -d
   ```
5. Server akan berjalan secara otomatis di port `5001`.

---

## ⚙️ Cara Menjalankan (Manual)

### Jika memilih Backend Python (`backend/python`)
```bash
cd backend/python
pip install -r requirements.txt
python main.py
```

### Jika memilih Backend Node.js (`backend/js`)
```bash
cd backend/js
npm install
npm run download-models
npm start
```

*Server API akan berjalan di alamat `http://0.0.0.0:5001`.*

---

## 📱 Android Configuration

Pastikan URL API di kode Android Anda (`MainActivity.java`) sudah sesuai dengan IP komputer/laptop Anda:

```java
private final String url = "http://YOUR_LOCAL_IP:5001/predict";
```
*(Gunakan IP lokal seperti `192.168.x.x` dan bukan `localhost` jika melakukan pengujian dari HP fisik).*

---

## ⚠️ Disclaimer

Aplikasi ini tidak ditujukan sebagai alat diagnosis psikologis atau medis.  
Hasil deteksi emosi dapat dipengaruhi oleh faktor pencahayaan, kemiringan wajah, kualitas kamera, dan atribut yang menutupi wajah (seperti kacamata tebal atau masker).

---

## ⭐ Support

Jika proyek aplikasi dan AI multi-backend ini membantu Anda belajar, jangan lupa kasih **star ⭐** di repositori GitHub ini ya!
