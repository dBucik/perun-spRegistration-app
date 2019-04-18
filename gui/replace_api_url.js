const fs = require('fs');
const file = 'src/environments/environment.prod.ts';
const apiUrl = process.argv[2];

fs.readFile(file, 'utf8', function (err,data) {
    if (err) {
        return console.log(err);
    }
    const result = data.replace(/(.*)api_url(.*)/g, '    api_url: \'' + apiUrl + '\',');

    fs.writeFile(file, result, 'utf8', function (err) {
        if (err) return console.log(err);
    });
});