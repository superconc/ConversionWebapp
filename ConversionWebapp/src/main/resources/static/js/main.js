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

// Gestione dei pulsanti e stato interno
const formatButtons = document.getElementById('formatButtons');
const toJpgButton = document.getElementById('toJpg');
const toPngButton = document.getElementById('toPng');
const toTiffButton = document.getElementById('toTiff');
const toGifButton = document.getElementById('toGif');
const toPdfButton = document.getElementById('toPdf');
const convertButton = document.getElementById('convertButton');
const resetButton = document.getElementById('resetButton');
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
	// Aggiorna l'area dropzone per mostrare i nomi dei file caricati
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

// Funzione per resettare la UI e lo stato interno
function resetUI() {
	// Ripristina il testo predefinito della dropzone
	dropzone.textContent = "Trascina le tue immagini qui o clicca per selezionarle";
	// Nascondi il container dei pulsanti di formato
	formatButtons.style.display = 'none';
	// Disabilita il pulsante Converti
	convertButton.disabled = true;
	convertButton.classList.add('disabled');
	// Svuota il file input
	fileInput.value = "";
	// Ripristina le variabili interne
	formatoOrigine = null;
	formatoDestinazione = null;
	selectedFiles = null;
	// Pulisci l'area dei messaggi
	resultMessage.innerHTML = "";
}

// Aggiungi listener per il pulsante Reset
resetButton.addEventListener('click', () => {
	console.log("Reset UI triggered");
	resetUI();
});

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
	const formData = new FormData();
	for (let i = 0; i < selectedFiles.length; i++) {
		formData.append('files', selectedFiles[i]);
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

		.then(data => {
			let html = `<p class="result-message">${data.message}</p>`;
			if (data.downloadLinks && data.downloadLinks.length > 0) {
				// usa la classe "download-list" per rimuovere i bullet
				html += `<ul class="download-list">`;
				data.downloadLinks.forEach((link, index) => {
					const fileName = data.fileNames ? data.fileNames[index] : link.split("file=")[1];
					html += `
		        <li>
		          <button class="action-button download-button" onclick="window.location.href='${link}'">
		            <img src="/images/download.png" alt="download" class="download-icon">
		            ${fileName}
		          </button>
		        </li>`;
				});
				html += `</ul>`;
				//		    html += `<button id="downloadAllBtn" class="action-button">Scarica Tutti</button>`;
				html += `<button id="downloadAllBtn" class="action-button">
               <img src="/images/zipImage.png" alt="zip" class="download-icon">
               Scarica Tutti
             </button>`;
			}
			resultMessage.innerHTML = html;

			// gestione del pulsante "Scarica Tutti"
			const downloadAllBtn = document.getElementById('downloadAllBtn');
			if (downloadAllBtn && data.fileNames) {
				downloadAllBtn.addEventListener('click', () => {
					const filesParam = data.fileNames.join(',');
					window.location.href = `/downloadAll?files=${encodeURIComponent(filesParam)}`;
				});
			}
		})

		.catch(err => {
			console.error(err);
			alert('Si è verificato un errore durante la conversione.');
		});
});

// pdf tab switching
const pdfTabs = ['unisci','taglia','comprimi','converti'];
pdfTabs.forEach(tab => {
  const menu = document.getElementById(`tab-${tab}`);
  const content = document.getElementById(`content-${tab}`);
  menu.addEventListener('click', e => {
    e.preventDefault();
    // nascondi tutte
    pdfTabs.forEach(t => {
      document.getElementById(`content-${t}`).style.display = 'none';
      document.getElementById(`tab-${t}`).classList.remove('active');
    });
    // mostra selezionata
    content.style.display = 'block';
    menu.classList.add('active');
  });
});

//// sottomenu pdf
//const tabs = ['unisci', 'taglia', 'comprimi', 'converti'];
//tabs.forEach(tab => {
//	document.getElementById(`tab-${tab}`)
//		.addEventListener('click', e => {
//			e.preventDefault();
//			// nascondi tutte le sezioni
//			tabs.forEach(t => {
//				document.getElementById(`content-${t}`).style.display = 'none';
//				document.getElementById(`tab-${t}`).classList.remove('active');
//			});
//			// mostra la sezione selezionata
//			document.getElementById(`content-${tab}`).style.display = 'block';
//			document.getElementById(`tab-${tab}`).classList.add('active');
//		});
//});
//
//
//// all’interno di main.js, dopo i listener esistenti:
//const pdfTabs = ['unisci','taglia','comprimi','converti'];
//pdfTabs.forEach(tab => {
//  document.getElementById(`tab-${tab}`)
//    .addEventListener('click', e => {
//      e.preventDefault();
//      // nascondi tutte le sezioni
//      pdfTabs.forEach(t => {
//        document.getElementById(`content-${t}`).style.display = 'none';
//        document.getElementById(`tab-${t}`).classList.remove('active');
//      });
//      // mostra solo il contenuto selezionato
//      document.getElementById(`content-${tab}`).style.display = 'block';
//      document.getElementById(`tab-${tab}`).classList.add('active');
//    });
//});
//
