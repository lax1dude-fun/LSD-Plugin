const http = require("http");
const archiver = require("archiver");
const config = require("./config.json");
const fs = require("fs");
const path = require("path");

const archive = archiver('zip', {
    zlib: { level: 9 }
});

const output = fs.createWriteStream(path.join(__dirname, "/LSD.zip"));

archive.pipe(output);

archive.directory("LSD/", false);

archive.finalize();

let server = http.createServer((req, res) => {
    fs.readFile("./LSD.zip", (err, file) => {
        if (err) throw err;
        res.end(file);
    });
});

server.listen(config.port);