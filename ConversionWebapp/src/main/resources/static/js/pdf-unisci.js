// elementi
const dropzone = document.getElementById('pdf-dropzone');
const pdfInput = document.getElementById('pdfInput');
const previewList = document.getElementById('previewList');
const mergeButton = document.getElementById('mergeButton');
const resetButton = document.getElementById('resetButton');

let pdfFiles = [];

// apri dialog
dropzone.addEventListener('click', () => pdfInput.click());

// drag & drop
;['dragover','dragenter'].forEach(evt => {
  dropzone.addEventListener(evt, e => {
    e.preventDefault();
    dropzone.classList.add('hover');
  });
});
;['dragleave','drop'].forEach(evt => {
  dropzone.addEventListener(evt, e => {
    e.preventDefault();
    dropzone.classList.remove('hover');
    if (evt === 'drop') handleFiles(e.dataTransfer.files);
  });
});
pdfInput.addEventListener('change', e => handleFiles(pdfInput.files));

// gestisci upload
function handleFiles(files) {
  const arr = Array.from(files).filter(f => f.type === 'application/pdf');
  if (arr.length + pdfFiles.length > 10) {
    alert('massimo 10 pdf');
    return;
  }
  pdfFiles = pdfFiles.concat(arr);
  renderPreviews();
  toggleMerge();
}

// mostra anteprime (placeholder thumb)
function renderPreviews() {
  previewList.innerHTML = '';
  pdfFiles.forEach((file, i) => {
    const item = document.createElement('li');
    item.className = 'pdf-preview-item';
    item.dataset.index = i;
    // qui potresti usare PDF.js per estrarre la prima pagina come immagine
    item.innerHTML = `
      <img src="/images/pdf-placeholder.png" class="pdf-preview-thumb" alt="pdf">
      <div class="pdf-preview-info">${file.name}<br>${'…pagine'}</div>
    `;
    previewList.append(item);
  });
  // abilita drag sort
  Sortable.create(previewList, {
    animation: 150,
    onEnd: evt => {
      const [moved] = pdfFiles.splice(evt.oldIndex, 1);
      pdfFiles.splice(evt.newIndex, 0, moved);
      renderPreviews();
    }
  });
}

// abilita/disabilita merge
function toggleMerge() {
  if (pdfFiles.length >= 2) {
    mergeButton.disabled = false;
    mergeButton.classList.remove('disabled');
  } else {
    mergeButton.disabled = true;
    mergeButton.classList.add('disabled');
  }
}

// reset
resetButton.addEventListener('click', () => {
  pdfFiles = [];
  previewList.innerHTML = '';
  toggleMerge();
});

// azione merge (da implementare)
mergeButton.addEventListener('click', () => {
  if (pdfFiles.length < 2) return;
  const formData = new FormData();
  pdfFiles.forEach(file => formData.append('files', file));
  fetch('/pdf/merge', {
    method: 'POST',
    body: formData
  })
  .then(r => r.json())
  .then(data => {
    // gestisci link di download JSON { downloadLink: "..."}
    window.location.href = data.downloadLink;
  })
  .catch(() => alert('errore unione pdf'));
});
