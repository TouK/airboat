# Airboat

Airboat is a no-ceremony code review app, firstly developed during summer internships (2012) at TouK.

More on the conception behind the app can be read [here] (https://github.com/TouK/codereview/wiki/Wizja) (Polish only)

# Demo
is available [here](http://cr.touk.pl/demo) (new version is deployed about-weekly, all data is deleted).

Also, continuous deployment is made to [this] (http://cr-cd.touk.pl/cd) place.

Any contributions (bug reports / feature requests / pull requests) are most welcome!

# Frameworks and libraries used
...which you might like to be aware of:

* Grails 2.1.0 (be sure to upgrade your grails installation, else the project won't build)
* jQuery
* Twitter Bootstrap
* [js-views] (https://github.com/BorisMoore/jsviews/) & [js-render] (https://github.com/BorisMoore/jsrender) - bleeding edge :)

## A note for IntelliJ IDEA users
Since there's a bug in IDEA < 11.3 in Grails support (namely: Grails 2.1.0 support...) you've got to **update your IDEA to import the project**... (both of which you already should have done anyway ;))

# How do I...
 
## ...import my project to Airboat?
First, you login as admin@codereview.pl (password: 'admin' :)). Then, you grasp the 'Admin page' near the 'log out' link.
 After that everything is straightforward. After adding a project you might need to wait a bit till it gets imported
(We're working on showing some kind of notification during the import)