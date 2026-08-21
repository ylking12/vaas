export function createLatestOnlyExecutor() {
  let sequence = 0

  return async function runLatest(run, apply) {
    const current = ++sequence
    const result = await run()

    if (current !== sequence) {
      return { applied: false, result }
    }

    apply(result)
    return { applied: true, result }
  }
}
