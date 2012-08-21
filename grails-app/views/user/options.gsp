<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
  <title>Options</title>
</head>
<body>

<script id="skinOptionTemplate" type="text/x-jsrender">
    <li><a href="#" rel="{{>href}}" onclick="changeSkin('{{>href}}', '{{>name}}')">{{>name}}</a></li>
</script>

<script type="text/javascript">

    var baseUrl =  "${createLink(uri: '/libs/bootstrap/themes/')}";

    var skins = [{href: baseUrl + 'default/bootstrap-default.css', name: "default"},
        {href: baseUrl + "cerulean/bootstrap-cerulean.css", name: "cerulean"},
        {href: baseUrl + "cyborg/bootstrap-cyborg.css", name: "cyborg"},
        {href: baseUrl + "journal/bootstrap-journal.css", name: "journal"},
        {href: baseUrl + "readable/bootstrap-readable.css", name: "readable"},
        {href: baseUrl + "simplex/bootstrap-simplex.css", name: "simplex"},
        {href: baseUrl + "slate/bootstrap-slate.css", name: "slate"},
        {href: baseUrl + "spacelab/bootstrap-spacelab.css", name: "spacelab"},
        {href: baseUrl + "united/bootstrap-united.css", name: "united"},
        {href: baseUrl + "touk/bootstrap-touk.css", name: "touk"}
    ]

    if($.cookies.get( 'skin' )) {
        $("#skin").attr("href", $.cookies.get( 'skin').href);
    }

    $(document).ready(function() {
        appendSkinOptions(skins);
    });

    function changeSkin(skinHref, skinName) {
            var skinOptions = {username: "anonymous", href: skinHref };
            var cookie = $.cookies.get( 'skin' );
            if(!cookie) {
                $.cookies.set( 'skin', skinOptions );
            }
            else {
                skinOptions.username = cookie.username;
                $.cookies.set( 'skin', skinOptions );
            }
            $("#skin").attr("href",skinHref);
            changeUserSkin(skinName)
            return false;
    }

    function changeUserSkin(skinName){
        var username = codeReview.loggedInUserName
        $.post("${createLink(uri: '/user/setSkinOptions/')}", { username: username, skin: skinName});
    }

    function appendSkinOptions(skins) {
        var skinOptions = $("#skinOptionTemplate").render(skins);
        $("#options").append(skinOptions);
    }

</script>
<div class="span12 well-large" >
    <div class='container'>
        <div class="span6 offset4 well well-large">

            Hello, <sec:username/>!
            <h1>Set your preferences:</h1>
            <h2>Choose the skin you like:</h2>

            <ul id="options">
            </ul>
            <a type="button" href="../../" class="btn btn-primary">Ok</a>

        </div>
    </div>
</div>
</body>
</html>