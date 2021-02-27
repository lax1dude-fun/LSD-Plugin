const fs = require("fs");
const modelData = require("./models.json").models;
const out = require("./models.json").out;
const process = require("process");

process.chdir(out);

let customModelLists = {};
let modelJSONContents = [];

modelData.forEach(modelDatum => {
    let modelDatumParts = modelDatum.split(":");
    let itemName = modelDatumParts[0];
    let customModelData = parseInt(modelDatumParts[1]);
    let customItemName = modelDatumParts[2];
    if (!customModelLists[itemName]) {
        customModelLists[itemName] = [];
    }
    customModelLists[itemName].push({
        data: customModelData,
        name: customItemName
    });

    modelJSONContents.push({
        filename: `${customItemName}.json`,
        content: {
            "parent": "item/generated",
            "textures": {
                "layer0": `item/${customItemName}`
            }
        }
    });

});

modelJSONContents.forEach(modelDesc => {
    fs.writeFile(modelDesc.filename, JSON.stringify(modelDesc.content), (err) => {
        if (err) throw err;
    });
});

Object.keys(customModelLists).forEach(existingItem => {
    fs.writeFile(`${existingItem}.json`, JSON.stringify({
        "parent": "item/generated",
        "textures": {
            "layer0": `item/${existingItem}`
        },
        "overrides": customModelLists[existingItem].map(override => {
            return {
                "predicate": {
                    "custom_model_data": override.data
                },
                "model": `item/${override.name}`
            };
        })
    }), (err) => {
        if (err) throw err;
    });
});

