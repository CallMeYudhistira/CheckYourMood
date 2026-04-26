const https = require('https');
const fs = require('fs');
const path = require('path');

const modelsDir = path.join(__dirname, 'models');
if (!fs.existsSync(modelsDir)) {
    fs.mkdirSync(modelsDir);
}

const baseUrl = 'https://raw.githubusercontent.com/justadudewhohacks/face-api.js/master/weights/';
const filesToDownload = [
    'ssd_mobilenetv1_model-weights_manifest.json',
    'ssd_mobilenetv1_model-shard1',
    'ssd_mobilenetv1_model-shard2',
    'face_expression_model-weights_manifest.json',
    'face_expression_model-shard1'
];

const downloadFile = (fileName) => {
    return new Promise((resolve, reject) => {
        const dest = path.join(modelsDir, fileName);
        if (fs.existsSync(dest)) {
            console.log(`File ${fileName} already exists, skipping...`);
            return resolve();
        }
        
        console.log(`Downloading ${fileName}...`);
        const file = fs.createWriteStream(dest);
        https.get(baseUrl + fileName, function(response) {
            response.pipe(file);
            file.on('finish', function() {
                file.close(() => {
                    console.log(`Downloaded ${fileName}`);
                    resolve();
                });
            });
        }).on('error', function(err) {
            fs.unlink(dest, () => {});
            console.error(`Error downloading ${fileName}: ${err.message}`);
            reject(err);
        });
    });
};

const run = async () => {
    for (const file of filesToDownload) {
        await downloadFile(file);
    }
    console.log('All models downloaded successfully!');
};

run();
