const indexTable = document.getElementById("index");

function list(data) {
    data.forEach(uri => {
        indexTable.appendChild(newRow(uri));
    });
}

function newRow(uri) {
    const tr = document.createElement('tr');
    tr.appendChild(newColIndex(uri));
    tr.appendChild(newColShow(uri));
    return tr;
}

function newColIndex(uri) {
    return newCol(uri, uri);
}

function newColShow(uri) {
    return newCol(uri + "show", "show");
}

function newCol(href, text) {
    const td = document.createElement('td');
    const a = document.createElement('a');
    a.href = href;
    a.textContent = text;
    td.appendChild(a);
    return td;
}

fetch("index.json")
    .then(resp => {
        if (!resp.ok) throw new Error("JSON konnte nicht geladen werden: " + resp.statusText);
        return resp.json();
    })
    .then(data => {
        list(data);
    })
    .catch(err => {
        console.error("Fehler beim Laden der JSON-Datei:", err);
        //indexSpan.textContent = "0/0";
        //showImage(0);
        //fileSpan.textContent = jsonFile + " (failed)";
    });
