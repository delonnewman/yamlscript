let fs = require("fs");
let YS = require("@yaml/yamlscript");

let input = fs.readFileSync("some.ys", "utf8");

let ys = new YS();

let data = ys.load(input);

console.log(data);
