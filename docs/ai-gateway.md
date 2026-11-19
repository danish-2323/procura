# Procura 2.0: AI Gateway Architecture

## Principles
1. **Advisory Only**: AI advises, deterministic business code decides. AI cannot approve, reject, match or disburse money.
2. **Kill Switch**: Setting `ai.enabled=false` immediately disables all AI endpoints, causing them to return `503 AI_UNAVAILABLE`. Core operations run completely unaffected.
3. **Mock First in CI**: The test suite uses `MockLlmProvider` to ensure zero external network calls during automated builds.
4. **Data Isolation**: Untrusted document inputs are strictly delimited, prompt-injection mitigated, and schema-validated.

## Provider Tiers
- **MVP Slice (Phase 7)**: Single text provider (Groq) + Gemini vision for invoice OCR fallback.
- **Post-MVP (P1/P2)**: Fallback chain: Groq -> Cerebras -> Mistral -> Gemini Flash -> OpenRouter :free.
