var replace = require('replace-in-file');
var package = require("./package.json");

var apiUrl = process.argv[2];

const options = {
    files: 'src/environments/environment.prod.ts',
    from: /api_url: '(.*)'/g,
    to: "api_url: '"+ apiUrl + "'",
    allowEmptyPaths: false,
};

try {
    let changedFiles = replace.sync(options);
    if (changedFiles == 0) {
        throw "Please make sure that file '" + options.files + "' has \"api_url: ''\"";
    }
    console.log('Api URL set: ' + apiUrl);
}
catch (error) {
    console.error('Error occurred:', error);
    throw error
}
