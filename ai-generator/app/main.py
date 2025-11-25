from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import JSONResponse
import pdfplumber
from openai import OpenAI
import json
from dotenv import load_dotenv
import os

load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = FastAPI()

client = OpenAI()


@app.post("/api/pdf/generate-questions/")
async def generate_questions(
    file: UploadFile = File(...),
    model: str = Form("gpt-4o-mini"),
    question_types: str = Form("[]"),
    num_questions: int = Form(10),
    difficulty: str = Form("medium"),
    language: str = Form("ko")
):
    try:
        # PDF 읽기
        pdf_text = ""
        with pdfplumber.open(file.file) as pdf:
            for page in pdf.pages:
                pdf_text += page.extract_text() + "\n"

        question_types_list = json.loads(question_types)

        prompt = f"""
        너는 교육 전문가이자 문제 생성 AI assistant이다.

        PDF 내용:
        {pdf_text}

        문제 생성 설정:
        - 문제 유형: {question_types_list}
        - 개수: {num_questions}
        - 난이도: {difficulty}
        - 언어: {language}

        위 조건으로 문제를 생성하고 JSON 형식으로 출력해라.
        """

        completion = client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": prompt}],
            response_format={"type": "json_object"}
        )

        return JSONResponse(content=json.loads(completion.choices[0].message.content))

    except Exception as e:
        return JSONResponse(status_code=400, content={"error": str(e)})



@app.post("/api/learning/generate-focus-questions/")
async def generate_focus_questions(payload: dict):

    if "error_stats" not in payload or len(payload["error_stats"]) == 0:
        return JSONResponse(status_code=400, content={"error": "error_stats 필드가 비어 있습니다."})

    prompt = f"""
    너는 교육 평가 AI이다.

    학생의 오답률 통계를 기반으로 취약한 개념을 분석하고,
    집중 학습용 문제를 JSON으로 생성하라.

    입력:
    {payload}
    """

    completion = client.chat.completions.create(
        model=payload.get("model", "gpt-4o-mini"),
        messages=[{"role": "user", "content": prompt}],
        response_format={"type": "json_object"}
    )

    return JSONResponse(content=json.loads(completion.choices[0].message.content))
