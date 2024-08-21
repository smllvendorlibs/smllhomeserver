
DROP TABLE IF EXISTS Packages;    

CREATE TABLE IF NOT EXISTS Packages(
	package_id INTEGER PRIMARY KEY, 
	package_name VARCHAR(30),
	author VARCHAR(30), 
	authorgiturl VARCHAR(120),
	repourl varchar(120),
	description varchar(300)
);

insert into Packages values (
	0, 
	'RaySmll', 
	'Hexaredecimal', 
	'https://github.com/hexaredecimal', 
	'https://github.com/smllvendorlibs/RaySmll', 
	'Bindings for the raylib library for smll'
);

insert into Packages values (
	1, 
	'Swing', 
	'Hexaredecimal', 
	'https://github.com/hexaredecimal', 
	'https://github.com/smllvendorlibs/Swing', 
	'Swing Gui library for smll'
);

