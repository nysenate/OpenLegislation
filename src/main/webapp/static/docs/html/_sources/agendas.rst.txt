**Senate Agenda API**
=============

Committee Agendas are a collection of legislative meetings that take place to discuss bills and ultimately pass them to the floor.

The committee agendas for a given week are contained within a collection known as the *weekly agenda*. The weekly agenda
starts at 1 for the first week, and increments by 1 for every subsequent week. The numbering of the agendas resets at the
start of every year. Therefore a weekly agenda can be uniquely identified using the *Agenda No* and a *Year*.

Get a single agenda
-------------------

**Usage**

Retrieve an agenda by year and agenda no
::
    (GET) /api/3/bills/{year}/{agendaNo}

Documentation coming soon..




