# CheckYourMood

## AI Mood Checker App (Android + Python)

CheckYourMood adalah aplikasi pendeteksi mood berbasis AI yang menganalisis ekspresi wajah melalui kamera dan memprediksi emosi menggunakan DeepFace sebagai backend (Python Flask) dan Android Studio (Java) sebagai frontend.

Project ini dibuat tanpa database dan berfokus pada analisis ekspresi wajah secara real-time.

---

## 🚀 Features

- Capture foto menggunakan kamera Android
- Deteksi emosi wajah menggunakan AI (DeepFace)
- Mapping emosi ke Bahasa Indonesia + deskripsi
- Loading indicator saat proses analisis
- Simpan hasil (foto + teks mood) ke Gallery
- Backend REST API menggunakan Flask

Emosi yang didukung:
- Bahagia 😁
- Sedih 😔
- Marah 😡
- Terkejut 😧
- Takut 😰
- Jijik 🤮
- Netral 😑

---

## 🧠 Tech Stack

### Frontend (Android)
- Java
- Android Studio
- Camera Intent
- Volley (HTTP Request)
- MediaStore API

### Backend
- Python 3.10+
- Flask
- DeepFace
- OpenCV

---

## 📦 Requirements

### Android
- Android Studio
- Minimum SDK 24+
- Camera Permission

### Backend
- Python 3.10+
- OS Windows / Linux

---

## 📥 Installation

### Clone Repository

```bash
git clone https://github.com/CallMeYudhistira/CheckYourMood.git
cd CheckYourMood
```

---

## ⚙️ Backend Setup

### Masuk ke folder backend

```bash
cd backend
```

### Install dependencies

```bash
pip install flask deepface opencv-python numpy
```

---

## ▶️ Run Backend Server

```bash
python main.py
```

Server akan berjalan di:
```bash
http://0.0.0.0:5000
```

---

## 📱 Android Configuration

Pastikan URL API di Android sesuai dengan IP backend:

```bash
http://YOUR_LOCAL_IP:5000/predict
```

Gunakan IP lokal (bukan localhost) jika dites di HP fisik.

---

## 🗂 Folder Structure

CheckYourMood/
├─ android/
│  └─ app/
├─ backend/
│  ├─ app.py
│  ├─ venv/
│  └─ README.md
├─ README.md

---

## ⚠️ Disclaimer

Aplikasi ini tidak ditujukan sebagai alat diagnosis psikologis atau medis.  
Hasil deteksi emosi dapat tidak akurat tergantung pencahayaan, posisi wajah, dan kualitas kamera.

---

## ⭐ Support

Jika project ini membantu kamu belajar Android atau AI, jangan lupa kasih star ⭐ di GitHub!
