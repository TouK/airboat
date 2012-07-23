/**
 * Created with IntelliJ IDEA.
 * User: touk
 * Date: 23.07.12
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
function popInfoBox (id)         {

    var identifier = id
    $(".show-changeset-button").colorbox({opacity:0.3 ,
        inline: true,
        width:"80%",
        height:"80%" ,
        onOpen:function(){
            $('#layer_content').html("") ;
            $('#layer_files').html("");
            var url = '${createLink(uri:'/changeset/getChangeset/')}';
            url = url.concat(id.toString());
            $.getJSON(url, function(data) {

                for(i = 0; i < data.length; i++) {
                    var changesets = {
                        author: data[i].author,
                        identifier: data[i].identifier,
                        date: data[i].date

                    }
                    $('#layer_content').append($("#changeset").render(changesets));

                }
            });
            var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
            fileUrl = fileUrl.concat(id.toString());
            $.getJSON(fileUrl, function(data) {
                for(i = 0; i < data.length; i++) {
                    var files = {
                        name: data[i].name,
                        identifier: data[i].id

                    }
                    $('#layer_files').append($("#project-files").render(files));

                }
            });
        },
        onLoad:function(){
            //code
        }
    });
}
function showCommentsToChangeset(id){
    $('#comments-'+id.toString()).html("");
    var fileUrl = '${createLink(uri:'/userComment/returnCommentsToChangeset/')}';
    fileUrl = fileUrl.concat(id.toString());
    $.getJSON(fileUrl, function(data) {
        for(i = 0; i < data.length; i++) {
            var comments = {
                author: data[i].author,
                date: data[i].dateCreated,
                content: data[i].content

            }
            var howManyComments = data.length;
            $('#comments-count-'+id.toString()).html(howManyComments.toString());
            $('#comments-'+id.toString()).append($("<h3>Comments: </h3>"));
            $('#comments-'+id.toString()).append($("#comment-template").render(comments));

        }
    });
}
function hideCommentsToChangeset(id){
    $('#comments-'+id.toString()).html("");
}

