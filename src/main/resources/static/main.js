async function generatePDF() {
    try {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'application/json';

        input.onchange = async (event) => {
            const file = event.target.files[0];
            if (!file) {
                throw new Error('No file selected');
            }

            const formData = new FormData();
            formData.append('file', file);

            const requestOptions = {
                method: 'POST',
                body: formData,
                redirect: 'follow',
            };

            const response = await fetch(
                `${window.location.origin}/api/generate-pdf`,
                requestOptions
            );

            if (!response.ok) {
                throw new Error(`Errore durante la richiesta: ${response.statusText}`);
            }

            const blob = await response.blob();
            const urlObject = URL.createObjectURL(blob);
            window.open(urlObject, '_blank');

            //const link = document.createElement('a');
            //link.href = urlObject;
            //link.download = 'report.pdf';

            //document.body.appendChild(link);
            //link.click();

            //URL.revokeObjectURL(urlObject);
            //document.body.removeChild(link);

            //console.log('Download completato.');
            console.log('Visualizzazione completata.');
        };

        input.click();

    } catch (error) {
        console.error('Si è verificato un errore:', error);
    }
}
