const express = require('express');
const cors = require('cors');
const canvas = require('canvas');
const faceapi = require('face-api.js');
const path = require('path');

// Monkey patch node env for face-api
const { Canvas, Image, ImageData } = canvas;
faceapi.env.monkeyPatch({ Canvas, Image, ImageData });

const app = express();
app.use(cors());
app.use(express.urlencoded({ limit: '50mb', extended: true }));

// Load models
const modelsPath = path.join(__dirname, 'models');
let modelsLoaded = false;

async function loadModels() {
    try {
        await faceapi.nets.ssdMobilenetv1.loadFromDisk(modelsPath);
        await faceapi.nets.faceExpressionNet.loadFromDisk(modelsPath);
        modelsLoaded = true;
        console.log("Models loaded successfully");
    } catch (err) {
        console.error("Error loading models:", err);
    }
}
loadModels();

const MOOD_MAP = {
    "happy": { label: "Bahagia 😁", description: "Wah lagi happy banget nih! Vibes-nya positif parah ✨🔥" },
    "sad": { label: "Sedih 😔", description: "Lagi down ya? Gapapa, kita jalanin sambil dengerin Frank Ocean 💙" },
    "angry": { label: "Marah 😡", description: "Wah kayaknya lagi panas nih. Chill dulu bentar woy, jangan sampe crashout 💨😤" },
    "surprised": { label: "Terkejut 😲", description: "Eh ada apa nih? Kayak baru kena plot twist 😳 Hati-hati jantungan wkwkwk" },
    "fearful": { label: "Takut 😰", description: "Lagi anxious mode ya? atau ada sesuatu? Santai aja, jangan panik 💪✨" },
    "disgusted": { label: "Jijik 🤢", description: "Wah kayak abis liat yang cringe abis 😅 Kalau nggak nyaman, mending skip aja!" },
    "neutral": { label: "Netral 😑", description: "Ekspresinya calm dan flat. Lagi santai aja atau lagi sigma? 🤔" }
};

app.post('/predict', async (req, res) => {
    if (!modelsLoaded) {
        return res.status(503).json({ error: "Models are still loading" });
    }

    try {
        const base64Data = req.body.image;
        if (!base64Data) {
            return res.status(400).json({ error: "No image provided" });
        }

        const imgBuffer = Buffer.from(base64Data, 'base64');
        const img = new Image();
        img.src = imgBuffer;

        const detections = await faceapi.detectSingleFace(img).withFaceExpressions();

        if (!detections) {
            return res.json({
                mood: "Wajah Tidak Terdeteksi",
                description: "Hmm, kami tidak bisa menemukan wajah di foto ini. Coba pastikan wajah terlihat jelas ya! 📸",
                confidence: 0.0
            });
        }

        const expressions = detections.expressions;
        let maxEmotion = "neutral";
        let maxConfidence = 0;

        for (const [emotion, confidence] of Object.entries(expressions)) {
            if (confidence > maxConfidence) {
                maxConfidence = confidence;
                maxEmotion = emotion;
            }
        }

        const confidencePercentage = maxConfidence * 100;

        if (confidencePercentage < 40.0) {
            return res.json({
                mood: "Kurang Jelas 🤔",
                description: "Ekspresinya agak susah ditebak nih. Coba foto ulang dengan cahaya yang lebih terang atau wajah menghadap lurus ya!",
                confidence: parseFloat(confidencePercentage.toFixed(2))
            });
        }

        const moodData = MOOD_MAP[maxEmotion] || { label: "Tidak Diketahui", description: "Ekspresi wajah tidak dapat dikenali." };

        res.json({
            mood: moodData.label,
            description: moodData.description,
            confidence: parseFloat(confidencePercentage.toFixed(2))
        });

    } catch (err) {
        console.error(err);
        res.status(500).json({
            mood: "Error",
            description: `Terjadi kesalahan saat memproses gambar: ${err.message}`,
            confidence: 0.0
        });
    }
});

const PORT = 5001;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Emotion API JS running on port ${PORT}`);
});
