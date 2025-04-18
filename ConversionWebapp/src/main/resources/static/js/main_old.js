// main.js

// Gestione navigazione menu
const menuImmagini = document.getElementById('menu-immagini');
const menuPdf = document.getElementById('menu-pdf');
const sezioneImmagini = document.getElementById('sezione-immagini');
const sezionePdf = document.getElementById('sezione-pdf');

menuImmagini.addEventListener('click', (event) => {
	event.preventDefault();
	sezioneImmagini.style.display = 'block';
	sezionePdf.style.display = 'none';
});
menuPdf.addEventListener('click', (event) => {
	event.preventDefault();
	sezionePdf.style.display = 'block';
	sezioneImmagini.style.display = 'none';
});

// Gestione area drag and drop
const dropzone = document.getElementById('dropzone');
const fileInput = document.getElementById('fileInput');

dropzone.addEventListener('click', () => fileInput.click());
dropzone.addEventListener('dragover', (e) => {
	e.preventDefault();
	dropzone.classList.add('hover');
});
dropzone.addEventListener('dragleave', () => dropzone.classList.remove('hover'));
dropzone.addEventListener('drop', (e) => {
	e.preventDefault();
	dropzone.classList.remove('hover');
	const files = e.dataTransfer.files;
	gestisciUpload(files);
});
fileInput.addEventListener('change', (e) => {
	const files = fileInput.files;
	gestisciUpload(files);
});

// Variabili per la gestione dei pulsanti e del formato
const formatButtons = document.getElementById('formatButtons');
const toJpgButton = document.getElementById('toJpg');
const toPngButton = document.getElementById('toPng');
const toTiffButton = document.getElementById('toTiff');
const toGifButton = document.getElementById('toGif');
const toPdfButton = document.getElementById('toPdf');
const convertButton = document.getElementById('convertButton');
const resultMessage = document.getElementById('resultMessage');

let formatoOrigine = null;
let formatoDestinazione = null;
let selectedFiles = null;

function gestisciUpload(files) {
	if (!files || files.length === 0) return;
	formatoOrigine = ottieniFormato(files[0].name);
	let fileNames = [];
	for (let i = 0; i < files.length; i++) {
		if (ottieniFormato(files[i].name) !== formatoOrigine) {
			alert('Tutti i file devono essere dello stesso formato!');
			return;
		}
		fileNames.push(files[i].name);
	}
	// Aggiorna il contenuto della dropzone per mostrare i nomi dei file caricati
	dropzone.textContent = "File caricati: " + fileNames.join(", ");
	formatButtons.style.display = 'block';
	abilitaFormatiDestinazione(formatoOrigine);
	selectedFiles = files;
}

function ottieniFormato(fileName) {
	const parts = fileName.split('.');
	return parts.length < 2 ? '' : parts[parts.length - 1].toLowerCase();
}

function abilitaFormatiDestinazione(orig) {
	[toJpgButton, toPngButton, toTiffButton, toGifButton, toPdfButton].forEach(btn => {
		btn.disabled = true;
		btn.classList.add('disabled');
	});
	if (orig !== 'jpg') {
		toJpgButton.disabled = false;
		toJpgButton.classList.remove('disabled');
	}
	if (orig !== 'png') {
		toPngButton.disabled = false;
		toPngButton.classList.remove('disabled');
	}
	if (orig !== 'tiff') {
		toTiffButton.disabled = false;
		toTiffButton.classList.remove('disabled');
	}
	if (orig !== 'gif') {
		toGifButton.disabled = false;
		toGifButton.classList.remove('disabled');
	}
	if (orig !== 'pdf') {
		toPdfButton.disabled = false;
		toPdfButton.classList.remove('disabled');
	}
	formatoDestinazione = null;
	convertButton.disabled = true;
	convertButton.classList.add('disabled');
}

toJpgButton.addEventListener('click', () => selezionaDestinazione('jpg'));
toPngButton.addEventListener('click', () => selezionaDestinazione('png'));
toTiffButton.addEventListener('click', () => selezionaDestinazione('tiff'));
toGifButton.addEventListener('click', () => selezionaDestinazione('gif'));
toPdfButton.addEventListener('click', () => selezionaDestinazione('pdf'));

function selezionaDestinazione(fmt) {
	formatoDestinazione = fmt;
	console.log('Formato destinazione: ' + formatoDestinazione);
	convertButton.disabled = false;
	convertButton.classList.remove('disabled');
}

// Evento per il pulsante Converti: utilizza fetch per inviare i dati al server
convertButton.addEventListener('click', () => {
	if (!formatoDestinazione) {
		alert('Seleziona prima un formato di destinazione.');
		return;
	}
	if (!selectedFiles || selectedFiles.length === 0) {
		alert('Nessun file caricato!');
		return;
	}
	// Crea l'oggetto FormData e invia il primo file (puoi estendere per gestire più file)
	//  const formData = new FormData();
	//  formData.append('file', selectedFiles[0]);
	//  formData.append('conversionType', formatoDestinazione);
	//  
	//  fetch('/uploadAjax', {
	//    method: 'POST',
	//    body: formData
	//  })
	//  .then(response => {
	//    if (!response.ok) {
	//      throw new Error("Errore durante la conversione");
	//    }
	//    return response.json();
	//  })
	//  .then(data => {
	//    resultMessage.innerHTML = `<p>${data.message}</p>`;
	//    if (data.downloadLink) {
	//      resultMessage.innerHTML += `<p><a href="${data.downloadLink}">Scarica il file convertito</a></p>`;
	//    }
	//  })
	//  .catch(err => {
	//    console.error(err);
	//    alert('Si è verificato un errore durante la conversione.');
	//  });


	// Modifica per inviare tutti i file
	const formData = new FormData();
	for (let i = 0; i < selectedFiles.length; i++) {
		formData.append('files', selectedFiles[i]); // 'files' come nome multiplo
	}
	formData.append('conversionType', formatoDestinazione);

	fetch('/uploadAjax', {
		method: 'POST',
		body: formData
	})
		.then(response => {
			if (!response.ok) {
				throw new Error("Errore durante la conversione");
			}
			return response.json();
		})
		//.then(data => {
		//  // Supponendo che il controller restituisca una lista di download link
		//  let html = `<p>${data.message}</p>`;
		//  if (data.downloadLinks) {
		//    html += `<ul>`;
		//    data.downloadLinks.forEach(link => {
		//      html += `<li><a href="${link}">Scarica il file convertito</a></li>`;
		//    });
		//    html += `</ul>`;
		//  }
		//  resultMessage.innerHTML = html;
		//})
		.then(data => {
			// Supponendo che 'data.downloadLinks' sia un array di URL (ad esempio: "/download?file=filename.png")
			// e che anche l'array 'data.fileNames' sia restituito dal server (contiene i nomi dei file convertiti).
			let html = `<p>${data.message}</p>`;
			if (data.downloadLinks && data.downloadLinks.length > 0) {
				html += `<ul>`;
				data.downloadLinks.forEach((link, index) => {
					// Estrae il nome del file (da link, oppure dal corrispondente data.fileNames[index])
					const fileName = data.fileNames ? data.fileNames[index] : link.split("file=")[1];
					html += `<li><a href="${link}">Scarica ${fileName}</a></li>`;
				});
				html += `</ul>`;
				// Aggiungi un pulsante "Scarica Tutto"
				html += `<button id="downloadAllBtn">Scarica Tutti</button>`;
			}
			resultMessage.innerHTML = html;

			// Aggiungi listener al pulsante Scarica Tutti (se presente)
			const downloadAllBtn = document.getElementById('downloadAllBtn');
			if (downloadAllBtn) {
				downloadAllBtn.addEventListener('click', () => {
					// Componi la query string con i nomi dei file, separati da virgola.
					// Ad esempio, se data.fileNames contiene ["file1.png","file2.png",...]
					const filesParam = data.fileNames.join(',');
					// Reindirizza il browser all'endpoint /downloadAll
					window.location.href = `/downloadAll?files=${encodeURIComponent(filesParam)}`;
				});
			}
		})

		.catch(err => {
			console.error(err);
			alert('Si è verificato un errore durante la conversione.');
		});

});
