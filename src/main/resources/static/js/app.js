// Wait for the page to fully load before running any code
$(document).ready(function () {

    // When the Generate button is clicked
    $('#generateBtn').on('click', function () {

        // Get and trim the prompt text
        const prompt = $('#promptInput').val().trim();

        // Validate — don't allow empty prompt
        if (!prompt) {
            showError('Please enter a prompt before generating.');
            return;
        }

        // Start the loading state
        setLoading(true);
        hideError();
        hidePreview();

        // Make a POST request to our Spring Boot backend
        $.ajax({
            url: '/api/generate',        // our endpoint
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ prompt: prompt }),  // send as JSON

            // SUCCESS — we got html, css, js back
            success: function (response) {
                setLoading(false);
                renderPreview(response.html, response.css, response.js);
            },

            // ERROR — something went wrong
            error: function (xhr) {
                setLoading(false);
                if (xhr.status === 400) {
                    showError('Invalid request. Please enter a valid prompt.');
                } else {
                    showError('Something went wrong. Please try again.');
                }
            }
        });
    });

    // Also trigger on Ctrl+Enter inside the textarea
    $('#promptInput').on('keydown', function (e) {
        if (e.ctrlKey && e.key === 'Enter') {
            $('#generateBtn').click();
        }
    });

});

// Inject the generated HTML, CSS, JS into the iframe
function renderPreview(html, css, js) {
    // Build a complete HTML document to inject into the iframe
    const fullDocument = `
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>${css}</style>
        </head>
        <body>
            ${html}
            <script>${js}<\/script>
        </body>
        </html>
    `;

    const iframe = document.getElementById('previewFrame');

    // Write the full document into the iframe
    iframe.contentDocument.open();
    iframe.contentDocument.write(fullDocument);
    iframe.contentDocument.close();

    // Show the preview section with a fade-in effect
    $('#previewSection').removeClass('d-none');
    setTimeout(() => $('#previewFrame').addClass('loaded'), 100);

    // Smooth scroll down to the preview
    $('html, body').animate({
        scrollTop: $('#previewSection').offset().top - 20
    }, 600);
}

// Show/hide loading spinner and disable button
function setLoading(isLoading) {
    if (isLoading) {
        $('#loadingSection').removeClass('d-none');
        $('#generateBtn').prop('disabled', true).text('Generating...');
    } else {
        $('#loadingSection').addClass('d-none');
        $('#generateBtn').prop('disabled', false).html('✨ Generate Website');
    }
}

function showError(message) {
    $('#errorMsg').removeClass('d-none').text(message);
}

function hideError() {
    $('#errorMsg').addClass('d-none').text('');
}

function hidePreview() {
    $('#previewSection').addClass('d-none');
    $('#previewFrame').removeClass('loaded');
}