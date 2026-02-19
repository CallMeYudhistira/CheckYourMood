from flask import Flask, request, jsonify
from deepface import DeepFace
import cv2
import numpy as np
import base64

app = Flask(__name__)

MOOD_MAP = {
    "happy": {
        "label": "Bahagia 😁",
        "description": "Wah lagi happy banget nih! Vibes-nya positif parah ✨🔥"
    },
    "sad": {
        "label": "Sedih 😔",
        "description": "Lagi down ya? Gapapa, kita jalanin sambil dengerin Frank Ocean 💙"
    },
    "angry": {
        "label": "Marah 😡",
        "description": "Wah kayaknya lagi panas nih. Chill dulu bentar woy, jangan sampe crashout 💨😤"
    },
    "surprise": {
        "label": "Terkejut 😲",
        "description": "Eh ada apa nih? Kayak baru kena plot twist 😳 Hati-hati jantungan wkwkwk"
    },
    "fear": {
        "label": "Takut 😰",
        "description": "Lagi anxious mode ya? atau ada sesuatu? Santai aja, jangan panik 💪✨"
    },
    "disgust": {
        "label": "Jijik 🤢",
        "description": "Wah kayak abis liat yang cringe abis 😅 Kalau nggak nyaman, mending skip aja!"
    },
    "neutral": {
        "label": "Netral 😑",
        "description": "Ekspresinya calm dan flat. Lagi santai aja atau lagi sigma? 🤔"
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
    app.run(host='0.0.0.0', port=5001)
