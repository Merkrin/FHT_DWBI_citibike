CREATE OR REPLACE
FUNCTION copy_data_to_new_tables() RETURNS void AS $$
DECLARE 
	trips_cursor CURSOR FOR
	SELECT
		*
	FROM
		trips;

	curr_date date;

	current_start_date_id integer;
	current_end_date_id integer;
	current_user_type_id integer;
	current_user_gender_id integer;
	current_user_birth_year_id integer;

BEGIN 
	FOR trip IN trips_cursor LOOP 
		curr_date := trip.start_date;

		INSERT
			INTO dim_date(
				"month",
				"year",
				"day"
			)
			VALUES(
				EXTRACT('Month' FROM curr_date),
				EXTRACT('Year' FROM curr_date),
				EXTRACT('Day' FROM curr_date)
			)
		ON CONFLICT DO NOTHING;
		
		SELECT date_id
			INTO current_start_date_id
			FROM dim_date
			WHERE dim_date."month" = EXTRACT('Month' FROM curr_date)
					AND dim_date."year" = EXTRACT('Year' FROM curr_date)
					AND dim_date."day" = EXTRACT('Day' FROM curr_date);
		
		curr_date := trip.end_date;
		
		INSERT
			INTO dim_date(
				"month",
				"year",
				"day"
			)
			VALUES(
				EXTRACT('Month' FROM curr_date),
				EXTRACT('Year' FROM curr_date),
				EXTRACT('Day' FROM curr_date)
			)
		ON CONFLICT DO NOTHING;
		
		SELECT date_id
			INTO current_end_date_id
			FROM dim_date
			WHERE dim_date."month" = EXTRACT('Month' FROM curr_date)
				AND dim_date."year" = EXTRACT('Year' FROM curr_date)
				AND dim_date."day" = EXTRACT('Day' FROM curr_date);
		
		INSERT
			INTO dim_user_type(user_type)
			SELECT user_type
				FROM users u
				WHERE u.user_id = trip.bike_user
		ON CONFLICT DO NOTHING;
		
		SELECT user_type_id
			INTO current_user_type_id
			FROM dim_user_type dut,
				 users u
			WHERE dut.user_type = u.user_type
				AND u.user_id = trip.bike_user;
		
		INSERT
			INTO dim_user_gender(gender)
			SELECT gender
				FROM users u
				WHERE u.user_id = trip.bike_user
		ON CONFLICT DO NOTHING;
		
		SELECT user_gender_id
			INTO current_user_gender_id
			FROM dim_user_gender dug,
				 users u
			WHERE dug.gender = u.gender
				AND u.user_id = trip.bike_user;
		
		INSERT
			INTO dim_user_birth_year(birth_year)
			SELECT birth_year
			FROM
				users u
			WHERE u.user_id = trip.bike_user
		ON CONFLICT DO NOTHING;
		
		SELECT user_birth_year_id
			INTO current_user_birth_year_id
			FROM dim_user_birth_year duby,
				 users u
			WHERE duby.birth_year = u.birth_year
				AND u.user_id = trip.bike_user;
		
		INSERT
			INTO fact_trip(
				start_date_id,
				end_date_id,
				start_station_id,
				end_station_id,
				user_type_id,
				user_gender_id,
				user_birth_year_id,
				trip_duration
			)
			VALUES(
				current_start_date_id,
				current_end_date_id,
				trip.start_station,
				trip.end_station,
				current_user_type_id,
				current_user_gender_id,
				current_user_birth_year_id,
				trip.duration
			)
		ON CONFLICT DO NOTHING;
END LOOP;
END;

$$ LANGUAGE plpgsql;

SELECT copy_data_to_new_tables();