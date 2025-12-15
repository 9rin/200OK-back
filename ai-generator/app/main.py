from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
import pdfplumber
from openai import OpenAI
import json
from dotenv import load_dotenv
import os

load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = FastAPI()

# CORS 설정 - Spring Boot에서 접근 가능하도록
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

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
        
        # 문제 유형이 비어있으면 기본값
        if not question_types_list:
            question_types_list = ["객관식", "주관식"]

        prompt = f"""
너는 교육 전문가이자 문제 생성 AI assistant이다.

아래 PDF 내용을 기반으로 문제를 생성하라.

PDF 내용:
{pdf_text[:3000]}

문제 생성 조건:
- 문제 유형: {question_types_list}
- 문제 개수: {num_questions}개
- 난이도: {difficulty}
- 언어: {language}

**중요: 반드시 아래 JSON 형식으로만 응답하라. 다른 텍스트나 설명을 추가하지 마라:**

{{
  "question_count": {num_questions},
  "generated_questions": [
    {{
      "type": "객관식 또는 주관식",
      "question": "문제 내용을 여기에",
      "answer": "정답을 여기에"
    }}
  ]
}}

규칙:
1. generated_questions 배열에 정확히 {num_questions}개의 문제를 포함
2. 각 문제는 type, question, answer 필드만 포함
3. type은 {question_types_list} 중 하나만 사용
4. problemId 필드는 포함하지 않음
"""

        completion = client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": prompt}],
            response_format={"type": "json_object"}
        )

        response_content = json.loads(completion.choices[0].message.content)
        
        # 응답 검증 및 보정
        if "generated_questions" not in response_content:
            return JSONResponse(
                status_code=500,
                content={"error": "OpenAI 응답 형식이 올바르지 않습니다."}
            )
        
        # question_count 보정
        if "question_count" not in response_content:
            response_content["question_count"] = len(response_content["generated_questions"])
        
        return JSONResponse(content=response_content)

    except Exception as e:
        return JSONResponse(status_code=400, content={"error": str(e)})



@app.post("/api/learning/generate-focus-questions/")
async def generate_focus_questions(payload: dict):

    if "error_stats" not in payload or len(payload["error_stats"]) == 0:
        return JSONResponse(status_code=400, content={"error": "error_stats 필드가 비어 있습니다."})

    prompt = f"""
    너는 교육 평가 AI이다.

    학생의 오답률 통계를 기반으로 취약한 개념을 분석하고,
    집중 학습용 문제를 아래 JSON 형식으로 생성하라.

    반드시 이 형식을 그대로 지켜라:

    {{
    "question_count": N,
    "generated_questions": [
        {{
        "type": "concept",
        "question": "문제 내용",
        "answer": "정답"
        }}
    ]
    }}

    규칙:
    1. generated_questions는 반드시 N개의 문제만 포함
    2. type, question, answer만 포함
    3. 다른 텍스트 절대 추가하지 말 것

    입력:
    {payload}
    """

    completion = client.chat.completions.create(
        model=payload.get("model", "gpt-4o-mini"),
        messages=[{"role": "user", "content": prompt}],
        response_format={"type": "json_object"}
    )

    return JSONResponse(content=json.loads(completion.choices[0].message.content))
