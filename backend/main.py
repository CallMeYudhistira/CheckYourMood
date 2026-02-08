from flask import Flask, request, jsonify
from deepface import DeepFace
import cv2
import numpy as np
import base64

app = Flask(__name__)

MOOD_MAP = {
    "happy": {
        "label": "Bahagia 😁",
        "description": "Keliatan lagi senang nih. Pertahanin mood positifnya ya, vibes-nya dapet 🔥"
    },
    "sad": {
        "label": "Sedih 😔",
        "description": "Lagi nggak baik-baik aja ya. Gapapa, semua orang pernah di fase ini 🤍"
    },
    "angry": {
        "label": "Marah 😡",
        "description": "Kelihatannya lagi emosi. Coba tarik napas dulu, tenangin diri sebentar 😤"
    },
    "surprise": {
        "label": "Terkejut 😧",
        "description": "Sepertinya ada sesuatu yang bikin kaget. Semoga kagetnya hal baik ya 😲"
    },
    "fear": {
        "label": "Takut 😰",
        "description": "Kamu terlihat cemas atau takut. Ingat, kamu nggak sendirian 💪"
    },
    "disgust": {
        "label": "Jijik 🤮",
        "description": "Ekspresi menunjukkan rasa nggak nyaman. Jauhin hal yang bikin nggak enak 🤢"
    },
    "neutral": {
        "label": "Netral 😑",
        "description": "Ekspresi wajah terlihat datar. Lagi santai atau capek mungkin 😐"
    }
}

@app.route('/predict', methods=['POST'])
def predict():
    data = request.form['image']
    img_bytes = base64.b64decode(data)
    np_arr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    result = DeepFace.analyze(
        img,
        actions=['emotion'],
        enforce_detection=False
    )

    emotion_en = result[0]['dominant_emotion']
    confidence = max(result[0]['emotion'].values())

    mood_data = MOOD_MAP.get(
        emotion_en,
        {
            "label": "Tidak Diketahui",
            "description": "Ekspresi wajah tidak dapat dikenali dengan jelas."
        }
    )

    return jsonify({
        "mood": mood_data["label"],
        "description": mood_data["description"],
        "confidence": round(confidence, 2)
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
