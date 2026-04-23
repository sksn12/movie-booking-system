"""
영화 상영 데이터 자동 생성 스크립트
===========================================
생성 위치  : src/resource/movieDir/movie_yyyy-MM-dd.txt
파일 형식  : MovieDTO 필드 순서에 따라 '|' 구분자 사용
             movieId|title|genre|runningTime|theaterNo|startTime|endTime
생성 기간  : 스크립트 실행일 기준 -7일 ~ +10일 (총 18일)

상영관 구성
-----------
  1관  : 100석 (A~J 행, 1~10 열)
  2관  :  80석 (A~H 행, 1~10 열)
  3관  :  80석 (A~H 행, 1~10 열)
  4관  :  50석 (A~E 행, 1~10 열, 4DX)

movieId 형식 : {yyyyMMdd}_{theaterNo}_{슬롯순번(2자리)}
              예) 20260423_1_01
"""

import os
from datetime import date, datetime, timedelta

# ──────────────────────────────────────────
# 경로 설정 (스크립트 위치 기준 → src/resource/movieDir)
# ──────────────────────────────────────────
SCRIPT_DIR   = os.path.dirname(os.path.abspath(__file__))   # src/common
SRC_DIR      = os.path.dirname(SCRIPT_DIR)                  # src
MOVIE_DIR    = os.path.join(SRC_DIR, "resource", "movieDir")
DELIMITER    = "|"
DAYS_BEFORE  = 7   # 오늘 기준 -7일
DAYS_AFTER   = 10  # 오늘 기준 +10일

# ──────────────────────────────────────────
# 상영관 정보
# ──────────────────────────────────────────
THEATERS = {
    1: {"name": "1관",   "total_seats": 100},
    2: {"name": "2관",   "total_seats": 80},
    3: {"name": "3관",   "total_seats": 80},
    4: {"name": "4관(4DX)", "total_seats": 50},
}

# ──────────────────────────────────────────
# 영화 목록
# (title, genre, runningTime(분), price)
# ──────────────────────────────────────────
MOVIES = [
    ("인터스텔라",        "SF/드라마",    169),
    ("어벤져스: 엔드게임", "액션/SF",     181),
    ("파묘",             "공포/미스터리",  134),
    ("범죄도시4",         "액션/범죄",    109),
    ("듄: 파트2",        "SF/모험",      166),
    ("서울의 봄",        "드라마/역사",   141),
]

# ──────────────────────────────────────────
# 상영관별 시작 시간표 (HH:MM)
# 상영관이 많을수록 다른 영화를 배정
# ──────────────────────────────────────────
THEATER_SCHEDULE = {
    # 상영관: [(시작시간, 영화인덱스), ...]
    1: [("09:00", 0), ("12:00", 0), ("15:00", 0), ("18:30", 0), ("21:30", 0)],
    2: [("09:30", 1), ("13:00", 1), ("16:30", 1), ("20:00", 1)           ],
    3: [("10:00", 2), ("12:30", 3), ("15:00", 2), ("17:30", 3), ("20:00", 2)],
    4: [("11:00", 4), ("14:30", 4), ("18:00", 5), ("21:00", 4)           ],
}


def make_movie_id(target_date: date, theater_no: int, slot_idx: int) -> str:
    """movieId 생성: yyyyMMdd_상영관번호_슬롯순번(2자리)"""
    return f"{target_date.strftime('%Y%m%d')}_{theater_no}_{slot_idx:02d}"


def build_row(movie_id: str, movie: tuple, theater_no: int,
              start_dt: datetime, end_dt: datetime) -> str:
    """
    CSV 한 줄 생성
    movieId|title|genre|runningTime|theaterNo|startTime|endTime
    """
    title, genre, running_time = movie
    # LocalDateTime 호환 형식: yyyy-MM-ddTHH:mm
    start_str = start_dt.strftime("%Y-%m-%dT%H:%M")
    end_str   = end_dt.strftime("%Y-%m-%dT%H:%M")

    fields = [
        movie_id,
        title,
        genre,
        str(running_time),
        str(theater_no),
        start_str,
        end_str,
    ]
    return DELIMITER.join(fields)


def generate_for_date(target_date: date) -> list[str]:
    """
    특정 날짜의 모든 상영 정보 행 목록을 반환
    """
    rows = []
    for theater_no, slots in THEATER_SCHEDULE.items():
        for slot_idx, (time_str, movie_idx) in enumerate(slots):
            movie = MOVIES[movie_idx]
            running_time = movie[2]  # 분 단위 (title, genre, runningTime)

            # startTime / endTime 계산
            hour, minute = map(int, time_str.split(":"))
            start_dt = datetime(target_date.year, target_date.month, target_date.day, hour, minute)
            end_dt   = start_dt + timedelta(minutes=running_time)

            movie_id = make_movie_id(target_date, theater_no, slot_idx + 1)
            rows.append(build_row(movie_id, movie, theater_no, start_dt, end_dt))

    return rows


def write_file(target_date: date, rows: list[str]) -> None:
    """
    movie_yyyy-MM-dd.txt 파일 작성
    첫 줄에 헤더(#로 시작) 포함
    """
    filename = f"movie_{target_date.strftime('%Y-%m-%d')}.txt"
    filepath = os.path.join(MOVIE_DIR, filename)

    with open(filepath, "w", encoding="utf-8") as f:
        # 헤더 (Java에서 읽을 때 '#' 시작 줄은 무시하도록 처리 권장)
        f.write("# movieId|title|genre|runningTime|theaterNo|startTime|endTime\n")
        for row in rows:
            f.write(row + "\n")

    print(f"  [생성 완료] {filepath}  ({len(rows)}개 상영 편성)")


def main():
    # movieDir 폴더가 없으면 생성
    os.makedirs(MOVIE_DIR, exist_ok=True)

    today = date.today()
    total_days = DAYS_BEFORE + DAYS_AFTER + 1  # -7 ~ +10 총 18일
    print(f"\n{'='*60}")
    print(f"  영화 데이터 자동 생성 시작")
    print(f"  기준일  : {today}")
    print(f"  생성 기간: -{DAYS_BEFORE}일({today - timedelta(days=DAYS_BEFORE)}) ~ +{DAYS_AFTER}일({today + timedelta(days=DAYS_AFTER)})  총 {total_days}일")
    print(f"  저장 위치: {MOVIE_DIR}")
    print(f"{'='*60}")

    for offset in range(-DAYS_BEFORE, DAYS_AFTER + 1):   # -7일 ~ +10일
        target_date = today + timedelta(days=offset)
        rows = generate_for_date(target_date)
        write_file(target_date, rows)

    print(f"\n총 {total_days}개 파일 생성 완료!\n")


if __name__ == "__main__":
    main()
