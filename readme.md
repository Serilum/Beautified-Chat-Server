<h2>Beautified Chat Server</h2>
<p><a href="https://github.com/Serilum/Beautified-Chat-Server"><img src="https://serilum.com/assets/data/logo/beautified-chat-server.png"></a></p><h2>Download</h2>
<p>You can download Beautified Chat Server on CurseForge and Modrinth:</p><p>&nbsp;&nbsp;CurseForge: &nbsp;&nbsp;<a href="https://curseforge.com/minecraft/mc-mods/beautified-chat-server">https://curseforge.com/minecraft/mc-mods/beautified-chat-server</a><br>&nbsp;&nbsp;Modrinth: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://modrinth.com/mod/beautified-chat-server">https://modrinth.com/mod/beautified-chat-server</a></p>
<h2>Issue Tracker</h2>
<p>To keep a better overview of all mods, the issue tracker is located in a separate repository.<br>&nbsp;&nbsp;For issues, ideas, suggestions or anything else, please follow this link:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;-> <a href="https://serilum.com/url/issue-tracker">Issue Tracker</a></p>
<h2>Pull Requests</h2>
<p>Because of the way mod loader files are bundled into one jar, some extra information is needed to do a PR.<br>&nbsp;&nbsp;A wiki page entry about it is available here:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;-> <a href="https://serilum.com/url/pull-requests">Pull Request Information</a></p>
<h2>Mod Description</h2>
<p style="text-align:center"><a href="https://serilum.com/" rel="nofollow"><img src="https://github.com/Serilum/.cdn/raw/main/description/header/header.png" alt="" width="838" height="400"></a></p>
<p style="text-align:center"><a href="https://curseforge.com/members/serilum/projects" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/curseforge.svg" width="200"></a> <a href="https://modrinth.com/user/Serilum" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/modrinth.svg" width="200"></a> <a href="https://patreon.com/serilum" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/patreon.svg" width="200"></a> <a href="https://youtube.com/@serilum" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/youtube.svg" width="200"></a></p>
<p><strong><span style="font-size:24px">Requires the library mod&nbsp;<a style="font-size:24px" href="https://curseforge.com/minecraft/mc-mods/collective" rel="nofollow">Collective</a>.<br></span></strong><br><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/beautified-chat-server/a.jpg" width="288" height="50"></picture><br><br> <strong>&nbsp; &nbsp; &nbsp;This mod is part of <span style="color:#008000"><a style="color:#008000" href="https://curseforge.com/minecraft/modpacks/the-vanilla-experience" rel="nofollow">The Vanilla Experience</a></span>.</strong><br><span style="font-size:18px">This is the <strong>server variant</strong> of the Beautified Chat mod. It is highly configurable and allows users to change the chat input server-wide.&nbsp;The mod variant you're currently looking at just needs the mod in the server mod list. It will make changes to the chat for all connected players without the need to have it on their clients. There is also a timestamp feature to know when messages were sent.<br><strong><span style="color:#396;font-size:18px"><br>The client version is also available and is called <a style="font-size:18px;color:#396" href="https://curseforge.com/minecraft/mc-mods/beautified-chat-client" rel="nofollow">Beautified Chat [Client]</a>.</span></strong><br>Both the client and server mod are compatible with each other, server-side takes priority.<br><br><strong><span style="color:#f60;font-size:18px">In default Minecraft, all chat is in the format:</span></strong><br><span style="font-size:14px"><strong>&lt;Username&gt; Message</strong></span><br><br><strong><span style="color:#f60;font-size:18px">The mod by default changes this to:</span></strong><br><span style="font-size:14px"><strong>Timestamp | Username: Message</strong></span><br><br>The default colours can be seen below in the example image. Both the format and the colour codes can be changed with ease via the config. If you don't want the timestamp, just remove it in the&nbsp;<em>chatMessageFormat</em> config option.<br><br>The default timestamp format is 24-hours. If you want AM/PM, just input in the <em>timestampFormat</em> config option "<strong>hh:mm a</strong>" instead of the default "<strong>HH:mm</strong>".</span></p>
<p><span style="font-size:18px"><br></span><br><br><strong><span style="font-size:20px">Configurable:</span> <span style="color:#008000;font-size:14px"><a style="color:#008000" href="https://github.com/Serilum/.information/wiki/how-to-configure-mods" rel="nofollow">(&nbsp;how do I configure?&nbsp;)</a></span><br></strong><span style="font-size:12px"><strong>chatMessageFormat</strong>&nbsp;(default = "%timestamp% | %username%: %chatmessage%"): Variables: %timestamp% = timestamp set in timestampFormat. %username% = the username of the player who sent the message.</span><br><span style="font-size:12px"><strong>timestampFormat</strong>&nbsp;(default = "HH:mm"): Example time in formats: 5 seconds past 9 o' clock in the evening. *=Default. *(HH:mm) = 21:00, (HH:mm:ss) = 21:00:05, (hh:mm a) = 9:00 PM</span><br><br><span style="font-size:14px"><strong>The possible colour code values for the next 4 configs are:</strong></span><br><span style="font-size:14px">0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, 5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, 10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white.</span><br><span style="font-size:12px"><strong><br>chatTimestampColour</strong>&nbsp;(default = 8, min 0, max 15): The colour of the timestamp in the chat message.&nbsp;</span><br><span style="font-size:12px"><strong>chatUsernameColour</strong>&nbsp;(default = 2, min 0, max 15): The colour of the username in the chat messsage.</span><br><span style="font-size:12px"><strong>chatMessageColour</strong>&nbsp;(default = 15, min 0, max 15): The colour of the chat message.</span><br><span style="font-size:12px"><strong>chatOtherSymbolsColour</strong>&nbsp;(default = 7, min 0, max 15): The colour of the other symbols from chatMessageFormat. So everything except the variables.</span><br><br><br><span style="font-size:18px"><strong>The default format ("%timestamp% | %username%: %chatmessage%"):</strong></span><br><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/beautified-chat-server/b.jpg" width="780" height="148"></picture></p>
<p><br>------------------<br><br><span style="font-size:24px"><strong>You may freely use this mod in any modpack, as long as the download remains hosted within the CurseForge or Modrinth ecosystem.</strong></span><br><br><span style="font-size:18px"><a style="font-size:18px;color:#008000" href="https://serilum.com/" rel="nofollow">Serilum.com</a> contains an overview and more information on all mods available.</span><br><br><span style="font-size:14px">Comments are disabled as I'm unable to keep track of all the separate pages on each mod.</span><span style="font-size:14px"><br>For issues, ideas, suggestions or anything else there is the&nbsp;<a style="font-size:14px;color:#008000" href="https://github.com/Serilum/.issue-tracker" rel="nofollow">Github repo</a>. Thanks!</span><span style="font-size:6px"><br><br></span></p>
<p style="text-align:center"><a href="https://serilum.com/donate" rel="nofollow"><img src="https://github.com/Serilum/.cdn/raw/main/description/projects/support.svg" alt="" width="306" height="50"></a></p>