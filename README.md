# play-practice

### Dababase tables: user_mst, user_dtl

#### user_mst trigger (after insert)

    BEGIN
	INSERT into
		user_dtl
	VALUES(
		NEW.usercode,
		'tempAddress',
		'tempPhone',
		'notSettedYet',
		NOW(),
		NOW()
	);
    END

#### user_mst trigger (before delete)

    BEGIN
	delete
	from
		user_dtl
	where
		usercode = OLD.usercode;
    END
