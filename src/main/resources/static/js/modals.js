/* Transfer data from link to modal */
$('.delete').click(function () {
    var name = $(this).data('name');
    $("#delete #delete-name").text(name);
    var action = $(this).data('action');
    $("#delete #delete-form").attr('action', action);
});

/* Validate the input field and control buttons */
$('#delete').on('shown.bs.modal', function() {
    // reset the form
    $('#delete #delete-button').attr('disabled', true);
    $('#delete #delete-confirm').val("");
    $('#delete #delete-confirm').focus();

    // register handler for text field
    $('#delete #delete-confirm').keyup( function() {
        if($(this).val() == $("#delete #delete-name").text()){
            $('#delete #delete-button').attr('disabled', false);
        } else {
            $('#delete #delete-button').attr('disabled', true);
        }
    })
});