    
$(document).ready(function () {
    
    $('#keywords').autocomplete({
        minLength: 1,
        source: function (request, response) {
            $.ajax({
                url: '/searchAutocomplete',
                method: 'get',
                data: { term: request.term },
                dataType: 'json',
                success: function (data) {
                    response(data);
                    console.log(data);
                },
                error: function (err) {
                    alert(err);
                }
            });
        },
        focus: updateTextBox,
        select: updateTextBox
    })
    
    .autocomplete('instance')._renderItem = function (ul, item) {
        return $("<li></li>" )  
        .data( "item.autocomplete", item )  
        .append(
        `<li style="font-weight: bold; border: none; padding: 10px;">` +    
        `<img src='/bookImage?bookId=${item.bookId}' accept='image/*'/>` + ' ' + item.title + 
        `</li>`) 
            
        .appendTo( ul );  
    };
    function updateTextBox(event, ui) {
        var title = ui.item.title;
        $(this).val(title);
        return false;
    }
});
