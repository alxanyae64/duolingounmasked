# duolingounmasked: Duolingo Survival Mode

Have you ever wanted Duo to threaten you, just like in the memes? Duolingo Survival Mode intercepts all Duolingo notifications and replaces them with some spiced up messages.

Download: [duolingounmasked_v1.0.apk](https://github.com/mondior71/duolingounleashed/blob/master/duolingounmasked_v1.0.apk?raw=true)

> You can't learn French without studying! Neither spare your family from starving in captivity.

> Practise your Chinese pronunciation or I\'ll cut out your tongue.

> Study Spanish or I\'ll study your entrails.

> Start learning German to see your family again.

It also can detect the language you study and show some language-specific threats:

> Learn the meaning of \"pain\" in French or in real life. Your choice.

> Study German now and the Gestapo will treat you nicely.

> Hurry up, you fucking weeb. Japanese won\'t study itself.

This app uses the `NotificationListenerService` class to detect Duolingo notifications and delegates the notification sending to a `JobIntentService`.
I don't know if that's the best way to do it (basically, I have no idea of Android app development), so any suggestions/pull requests are welcome.

If you have any ideas for new messages, you can also put them into a pull request for `strings.xml`.

Credit for the messages mostly goes to my friends who are a lot more creative than I am.

**Note:** I am obviously not affliated with Duolingo in any way.
