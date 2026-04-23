import os

movie_dir = '/Users/choiwoojin/2026/bootcamp/movie-booking-system/src/resource/movieDir'
book_dir = '/Users/choiwoojin/2026/bootcamp/movie-booking-system/src/resource/bookDir'

if not os.path.exists(book_dir):
    os.makedirs(book_dir)

# 10x20 matrix of 'o' separated by commas
seat_row = ','.join(['o'] * 20)
matrix = '\n'.join([seat_row] * 10)

for filename in os.listdir(movie_dir):
    if filename.startswith('movie_') and filename.endswith('.txt'):
        date = filename[len('movie_'):-4] # Extract YYYY-MM-DD
        date_dir = os.path.join(book_dir, date)
        if not os.path.exists(date_dir):
            os.makedirs(date_dir)
        
        with open(os.path.join(movie_dir, filename), 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                movie_id = line.split(',')[0]
                book_filepath = os.path.join(date_dir, f"{movie_id}.txt")
                with open(book_filepath, 'w', encoding='utf-8') as bf:
                    bf.write(matrix + '\n')
                    
print("Booking data generated successfully!")
